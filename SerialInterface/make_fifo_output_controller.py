from veriloggen import *

from make_fifo import make_fifo


def make_fifo_output_controller(data_width_ext, serial_data_width):
    m = Module("fifo_output_controller")

    # Tag for out_controller
    CONTROL_MDATA = m.Parameter("CONTROL_MDATA", Int(0, serial_data_width, 10))

    # basic Signals
    clk = m.Input("clk")
    rst = m.Input("rst")
    start = m.Input("start")
    conf = m.Input("conf", 40)
    conf_valid = m.Input("conf_valid", 2)

    has_wr_pending = m.OutputReg("has_wr_pending")
    afu_user_done = m.Input("afu_user_done")

    # request write data signals
    req_wr_available = m.Input("req_wr_available")
    req_wr_mdata = m.Output("req_wr_mdata", serial_data_width)
    req_wr_data = m.Output("req_wr_data", data_width_ext)
    req_wr_en = m.OutputReg("req_wr_en")

    # wr_data confirm signals
    resp_wr_valid = m.Input("resp_wr_valid")
    resp_wr_mdata = m.Input("resp_wr_mdata", serial_data_width)

    # Data from AFU_USER
    afu_user_wr_available = m.Output("afu_user_wr_available")
    afu_user_wr_data = m.Input("afu_user_wr_data", data_width_ext)
    afu_user_wr_en = m.Input("afu_user_wr_en")

    wr_done = m.OutputReg("wr_done")

    # written control wire
    m.EmbeddedCode("//written control wire")
    written_valid = m.Wire("written_valid")
    written_valid.assign(AndList(resp_wr_mdata == CONTROL_MDATA, resp_wr_valid))

    m.EmbeddedCode("//MDATA for data write requests")
    req_wr_mdata.assign(CONTROL_MDATA)

    # Conf_controller wires and regs
    m.EmbeddedCode("//Conf_controller wires and regs")
    internal_rst = m.Reg("internal_rst")
    qtde_in = m.Reg("qtde_in", 32)

    # fifo_data_out wires and regs
    m.EmbeddedCode("//fifo_data_out wires and regs")
    fifo_out_rd_en = m.Reg("fifo_out_rd_en")
    fifo_out_empty = m.Wire("fifo_out_empty")
    fifo_out_almost_empty = m.Wire("fifo_out_almost_empty")
    fifo_out_full = m.Wire("fifo_out_full")
    fifo_out_almost_full = m.Wire("fifo_out_almost_full")
    fifo_out_valid = m.Wire("fifo_out_valid")
    fifo_out_count = m.Wire("fifo_out_count", 3)

    # Pending controller
    m.EmbeddedCode("//Pending controller")
    wr_pendings = m.Reg("wr_pendings", 4)
    user_pendings = m.Reg("user_pendings", 4)
    has_user_pending = m.Reg("has_user_pending")

    # received data controller wires and regs
    m.EmbeddedCode("//received data controller wires and regs")
    # counter_written = m.Reg("counter_written", 30)

    # Request controller wires and regs
    m.EmbeddedCode("//Request controller wires and regs")
    counter_req = m.Reg("counter_req", 30)
    issue_req_write = m.Wire("issue_req_write")
    issue_req_write.assign(Mux(fifo_out_almost_empty, ~fifo_out_empty & ~fifo_out_rd_en, Int(1, 1, 2)))

    # afu_user_rd_available
    m.EmbeddedCode("//afu_user_rd_available")
    afu_user_wr_available.assign(~fifo_out_almost_full)

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

    # written data controller
    '''m.EmbeddedCode("//written data controller")
    m.Always(Posedge(clk))(
        If(internal_rst)(
            counter_written(Int(0, counter_written.width, 10)),
        ).Else(
            If(written_valid)(
                counter_written(counter_written + Int(1, counter_written.width, 10))
            )
        )
    )'''

    # DONE controller
    m.EmbeddedCode("//DONE controller")
    m.Always(Posedge(clk))(
        If(internal_rst)(
            wr_done(Int(0, 1, 2)),
        ).Else(  # if(start && counter_written == counter_req && afu_user_done && user_pendings == 0) begin
            If(OrList(AndList(start, ~has_wr_pending, ~has_user_pending, afu_user_done, fifo_out_empty),
                      AndList(counter_req == qtde_in, ~has_wr_pending)))(
                wr_done(Int(1, 1, 2)),
            )
        )
    )

    # has_wr_pending, has_user_pending controller
    m.EmbeddedCode("//has_wr_pending controller")
    m.EmbeddedCode("//has_user_pending controller")
    m.Always(Posedge(clk))(
        If(internal_rst)(
            has_wr_pending(Int(0, 1, 2)),
            has_user_pending(Int(0, 1, 2)),
        ).Else(
            has_wr_pending(Mux(wr_pendings > 0, Int(1, 1, 2), Int(0, 1, 2))),
            has_user_pending(Mux(user_pendings > 0, Int(1, 1, 2), Int(0, 1, 2))),
        )
    )

    # Pending controller
    m.EmbeddedCode("//Pending controller")
    m.Always(Posedge(clk))(
        If(internal_rst)(
            wr_pendings(Int(0, wr_pendings.width, 2)),
        ).Else(
            Case(Cat(written_valid, req_wr_en))(
                When(Int(0, 2, 2))(
                    wr_pendings(wr_pendings),
                ),
                When(Int(1, 2, 2))(
                    wr_pendings(wr_pendings + Int(1, wr_pendings.width, 10)),
                ),
                When(Int(2, 2, 2))(
                    wr_pendings(wr_pendings - Int(1, wr_pendings.width, 10)),
                ),
                When(Int(3, 2, 2))(
                    wr_pendings(wr_pendings),
                ),
            )
        )
    )

    m.Always(Posedge(clk))(
        If(internal_rst)(
            user_pendings(Int(0, user_pendings.width, 2)),
        ).Else(
            Case(Cat(req_wr_en, afu_user_wr_en))(
                When(Int(0, 2, 2))(
                    user_pendings(user_pendings),
                ),
                When(Int(1, 2, 2))(
                    user_pendings(user_pendings + Int(1, user_pendings.width, 10)),
                ),
                When(Int(2, 2, 2))(
                    user_pendings(user_pendings - Int(1, user_pendings.width, 10)),
                ),
                When(Int(3, 2, 2))(
                    user_pendings(user_pendings),
                ),
            )
        )
    )

    # Req wr _data Controller
    m.EmbeddedCode("//Req wr _data Controller")
    m.Always(Posedge(clk))(
        If(internal_rst)(
            fifo_out_rd_en(Int(0, 1, 2)),
        ).Elif(~wr_done)(
            fifo_out_rd_en(Int(0, 1, 2)),
            If(AndList(issue_req_write, req_wr_available))(
                fifo_out_rd_en(Int(1, 1, 2)),
            )
        )
    )

    m.Always(Posedge(clk))(
        If(internal_rst)(
            req_wr_en(Int(0, 1, 2)),
            counter_req(Int(0, counter_req.width, 10)),
        ).Else(
            req_wr_en(Int(0, 1, 2)),
            If(fifo_out_valid)(
                req_wr_en(Int(1, 1, 2)),
                counter_req(counter_req + Int(1, counter_req.width, 10)),
            )
        )
    )

    fifo = make_fifo()
    # FIFO_Out
    m.EmbeddedCode("//FIFO_OUT")
    con = [("clk", clk), ("rst", internal_rst), ("we", afu_user_wr_en), ("din", afu_user_wr_data),
           ("re", fifo_out_rd_en),
           ("dout", req_wr_data), ("empty", fifo_out_empty), ("almostempty", fifo_out_almost_empty),
           ("full", fifo_out_full), ("almostfull", fifo_out_almost_full), ("count", fifo_out_count),
           ("valid", fifo_out_valid)]
    params = [("FIFO_WIDTH", data_width_ext), ("FIFO_DEPTH_BITS", 3)]
    m.Instance(fifo, "fifo_out", params, con)

    initial = m.Initial()
    initial.add(internal_rst(Int(1, 1, 2)))

    return m

# make_fifo_output_controller(32, 8).to_verilog("verilog")
