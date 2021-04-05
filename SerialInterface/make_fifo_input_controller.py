from veriloggen import *
from math import *

from make_fifo import make_fifo


def make_fifo_input_controller(data_width_ext, serial_data_width):
    m = Module("fifo_input_controller")

    # Tag for in_controller
    CONTROL_MDATA = m.Parameter("CONTROL_MDATA", Int(0, serial_data_width, 10))

    # basic Signals
    clk = m.Input("clk")
    rst = m.Input("rst")
    start = m.Input("start")
    conf = m.Input("conf", 40)
    conf_valid = m.Input("conf_valid",2)

    has_pending = m.OutputReg("has_pending")

    # request_signals
    req_rd_available = m.Input("req_rd_available")
    req_rd_mdata = m.Output("req_rd_mdata", serial_data_width)
    req_rd_en = m.OutputReg("req_rd_en")

    # in_data signals
    resp_rd_valid = m.Input("resp_rd_valid")
    resp_rd_mdata = m.Input("resp_rd_mdata", serial_data_width)
    resp_rd_data = m.Input("resp_rd_data", data_width_ext)

    # Data for AFU_USER
    afu_user_rd_available = m.Output("afu_user_rd_available")
    afu_user_rd_data = m.Output("afu_user_rd_data", data_width_ext)
    afu_user_rd_en = m.Input("afu_user_rd_en")
    afu_user_rd_valid = m.Output("afu_user_rd_valid")

    rd_done = m.OutputReg("rd_done")

    m.EmbeddedCode("//MDATA for data requests")
    req_rd_mdata.assign(CONTROL_MDATA)

    # Conf_controller wires and regs
    m.EmbeddedCode("//Conf_controller wires and regs")
    internal_rst = m.Reg("internal_rst")
    qtde_in = m.Reg("qtde_in", 32)

    # fifo_data_in wires and regs
    m.EmbeddedCode("//fifo_data_in wires and regs")
    fifo_in_wr_en = m.Reg("fifo_in_wr_en")
    fifo_in_wr_data = m.Reg("fifo_in_wr_data", data_width_ext)
    fifo_in_empty = m.Wire("fifo_in_empty")
    fifo_in_almost_empty = m.Wire("fifo_in_almost_empty")
    fifo_in_full = m.Wire("fifo_in_full")
    fifo_in_almost_full = m.Wire("fifo_in_almost_full")
    fifo_in_count = m.Wire("fifo_in_count", 3)

    # Pending controller
    m.EmbeddedCode("//Pending controller")
    user_diff = m.Reg("user_diff", 4)
    pendings = m.Reg("pendings", 4)

    # received data controller wires and regs
    m.EmbeddedCode("//received data controller wires and regs")
    counter_received = m.Reg("counter_received", 30)

    # Request controller wires and regs
    m.EmbeddedCode("//Request controller wires and regs")
    counter_req = m.Reg("counter_req", 30)
    fifo_fit = m.Wire("fifo_fit")
    fifo_fit.assign(user_diff < int(pow(2, 3)))
    issue_req_data = m.Wire("issue_req_data")
    issue_req_data.assign(start & fifo_fit & req_rd_available & (counter_req < qtde_in))

    #afu_user_rd_available
    m.EmbeddedCode("//afu_user_rd_available")
    afu_user_rd_available.assign(Mux(fifo_in_almost_empty, ~fifo_in_empty & ~afu_user_rd_en, Int(1, 1, 2)))

    # Conf_controller
    m.EmbeddedCode("//Conf Controller")
    m.Always(Posedge(clk))(
        If(rst)(
            qtde_in(Int(0, qtde_in.width, 10)),
            internal_rst(Int(1, 1, 2)),
        ).Elif(AndList(conf_valid[0], conf[0:8] == CONTROL_MDATA))(
            internal_rst(Int(0, 1, 2)),
            qtde_in(conf[8:40]),
        )
    )

    # received data controller
    m.EmbeddedCode("//received data controller")
    m.Always(Posedge(clk))(
        If(internal_rst)(
            fifo_in_wr_en(Int(0, 1, 2)),
            counter_received(Int(0, counter_received.width, 10)),
        ).Else(
            fifo_in_wr_en(Int(0, 1, 2)),
            If(AndList(resp_rd_valid, resp_rd_mdata == CONTROL_MDATA))(
                fifo_in_wr_en(Int(1, 1, 2)),
                fifo_in_wr_data(resp_rd_data),
                counter_received(counter_received + Int(1, counter_received.width, 10))
            )
        )
    )

    # DONE controller
    m.EmbeddedCode("//DONE controller")
    m.Always(Posedge(clk))(
        If(internal_rst)(
            rd_done(Int(0, 1, 2)),
        ).Else(
            If(AndList(start, qtde_in == counter_received))(
                rd_done(Int(1, 1, 2)),
            )
        )
    )

    #has_pending controller
    m.EmbeddedCode("//has_pending controller")
    m.Always(Posedge(clk))(
        If(internal_rst)(
            has_pending(Int(0, 1, 2)),
        ).Else(
            has_pending(Mux(pendings > 0, Int(1, 1, 2), Int(0, 1, 2))),
        )
    )

    # Pending controller
    m.EmbeddedCode("//Pending controller")
    m.Always(Posedge(clk))(
        If(internal_rst)(
            user_diff(Int(0, user_diff.width, 2)),
        ).Else(
            Case(Cat(afu_user_rd_en, issue_req_data))(
                When(Int(0, 2, 2))(
                    user_diff(user_diff),
                ),
                When(Int(1, 2, 2))(
                    user_diff(user_diff + Int(1, user_diff.width, 10)),
                ),
                When(Int(2, 2, 2))(
                    user_diff(user_diff - Int(1, user_diff.width, 10)),
                ),
                When(Int(3, 2, 2))(
                    user_diff(user_diff),
                ),
            )
        )
    )

    m.Always(Posedge(clk))(
        If(internal_rst)(
            pendings(Int(0, pendings.width, 2)),
        ).Else(
            Case(Cat(resp_rd_valid, issue_req_data))(
                When(Int(0, 2, 2))(
                    pendings(pendings),
                ),
                When(Int(1, 2, 2))(
                    pendings(pendings + Int(1, pendings.width, 10)),
                ),
                When(Int(2, 2, 2))(
                    pendings(pendings - Int(1, pendings.width, 10)),
                ),
                When(Int(3, 2, 2))(
                    pendings(pendings),
                ),
            )
        )
    )

    # Req_data Controller
    m.EmbeddedCode("//Req_data Controller")
    m.Always(Posedge(clk))(
        If(internal_rst)(
            req_rd_en(Int(0, 1, 2)),
            counter_req(Int(0, counter_req.width, 10)),
        ).Else(
            req_rd_en(Int(0, 1, 2)),
            If(issue_req_data)(
                req_rd_en(Int(1, 1, 2)),
                counter_req(counter_req + Int(1, counter_req.width, 10)),
            )
        )
    )

    fifo = make_fifo()
    # FIFO_IN
    m.EmbeddedCode("//FIFO_IN")
    con = [("clk", clk), ("rst", internal_rst), ("we", fifo_in_wr_en), ("din", fifo_in_wr_data), ("re", afu_user_rd_en),
           ("dout", afu_user_rd_data), ("empty", fifo_in_empty), ("almostempty", fifo_in_almost_empty),
           ("full", fifo_in_full), ("almostfull", fifo_in_almost_full), ("count", fifo_in_count),
           ("valid", afu_user_rd_valid)]
    params = [("FIFO_WIDTH", data_width_ext), ("FIFO_DEPTH_BITS", 3)]
    m.Instance(fifo, "fifo_in", params, con)


    initial = m.Initial()
    initial.add(internal_rst(Int(1, 1, 2)))

    return m


#make_fifo_input_controller(32, 8).to_verilog("verilog")
