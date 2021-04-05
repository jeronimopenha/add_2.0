from veriloggen import *

from make_arbiter import make_arbiter
from make_fifo import make_fifo
from util import numBits


def make_fifo_selector(data_width, qtde_in):
    m = Module("fifo_selector")

    clk = m.Input("clk")
    rst = m.Input("rst")

    data_in_re = m.OutputReg("data_in_re", qtde_in)
    data_in_data = m.Input("data_in_data", qtde_in * data_width)
    data_in_available = m.Input("data_in_available", qtde_in)
    data_in_valid = m.Input("data_in_valid", qtde_in)

    req_wr_en = m.OutputReg("req_wr_en")
    req_wr_data = m.OutputReg("req_wr_data", data_width)
    req_wr_available = m.Input("req_wr_available")

    data = m.Wire("data",data_width,qtde_in)

    for i in range(0,qtde_in):
        data[i].assign(data_in_data[i*data_width:(i*data_width) + data_width])

    m.EmbeddedCode("//Arbiter wires")
    grant = m.Wire("grant", qtde_in)
    grant_valid = m.Wire("grant_valid")
    grant_encoded = m.Wire("grant_encoded", numBits(qtde_in))

    arbiter = make_arbiter()
    con = [("clk", clk), ("rst", rst), ("request", data_in_available), ("acknowledge", Int(0, qtde_in, 10)),
           ("grant", grant),
           ("grant_valid", grant_valid), ("grant_encoded", grant_encoded)]
    params = [("PORTS", qtde_in), ("TYPE", "ROUND_ROBIN"), ("BLOCK", "NONE"), ("LSB_PRIORITY", "LOW")]
    m.Instance(arbiter, "arbiter%d" % qtde_in, params, con)


    fsm_selector = m.Reg("fsm_selector", 3)
    FSM_SELECTOR_IDLE = m.Localparam("FSM_SELECTOR_IDLE",Int(0,fsm_selector.width,10))
    FSM_SELECTOR_WRITE = m.Localparam("FSM_SELECTOR_WRITE",Int(1,fsm_selector.width,10))
    index = m.Reg("index", numBits(qtde_in))

    m.Always(Posedge(clk))(
        If(rst)(
            data_in_re(Int(0, data_in_re.width, 2)),
            req_wr_en(Int(0,1,2)),
            fsm_selector(FSM_SELECTOR_IDLE),
        ).Else(
            data_in_re(Int(0, data_in_re.width, 2)),
            req_wr_en(Int(0, 1, 2)),
            Case(fsm_selector)(
                When(FSM_SELECTOR_IDLE)(
                    If(AndList(grant_valid, req_wr_available))(
                        index(grant_encoded),
                        data_in_re[grant_encoded](Int(1, 1, 2)),
                        fsm_selector(FSM_SELECTOR_WRITE),
                    )
                ),
                When(FSM_SELECTOR_WRITE)(
                    If(data_in_valid[index])(
                        req_wr_en(Int(1, 1, 2)),
                        req_wr_data(data[index]),
                        fsm_selector(FSM_SELECTOR_IDLE),
                    )
                )
            )
        )
    )

    return m


#make_fifo_selector(40, 4).to_verilog("verilog")
