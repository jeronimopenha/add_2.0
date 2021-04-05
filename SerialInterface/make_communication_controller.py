
from veriloggen import *
from math import *

from make_fifo import make_fifo
from make_serial import make_serial


def make_communication_controller(data_width_ext, hw_info_num_bits, dms_num_bits, serial_data_width):
    m = Module("communication_controller")

    # basic Signals
    clk = m.Input("clk")
    rst = m.Input("rst")

    # Serial
    rx = m.Input("rx")
    tx = m.Output("tx")
    rx_busy = m.Output("rx_busy")
    tx_busy = m.Output("tx_busy")

    dsm = m.Input("dsm", dms_num_bits)
    conf = m.Output("conf", 40)
    conf_valid = m.Output("conf_valid", 2)

    hw_info = m.Input("hw_info", hw_info_num_bits)

    # protocol controller in
    rst_afus = m.Output("rst_afus", data_width_ext)
    start_afus = m.Output("start_afus", data_width_ext)

    resp_rd_valid = m.Output("resp_rd_valid")
    resp_rd_mdata = m.Output("resp_rd_mdata", serial_data_width)
    resp_rd_data = m.Output("resp_rd_data", data_width_ext)

    req_rd_en = m.Input("req_rd_en")
    req_rd_mdata = m.Input("req_rd_mdata", serial_data_width)
    req_rd_available = m.Output("req_rd_available")

    # protocol controller out
    resp_wr_mdata = m.Output("resp_wr_mdata", serial_data_width)
    resp_wr_valid = m.Output("resp_wr_valid")

    req_wr_en = m.Input("req_wr_en")
    req_wr_available = m.Output("req_wr_available")
    req_wr_data = m.Input("req_wr_data", data_width_ext + serial_data_width)

    # Serial Wires
    m.EmbeddedCode("//Serial Wires")
    serial_wr_available = m.Wire("serial_wr_available")
    serial_wr_en = m.Wire("serial_wr_en")
    serial_wr_data = m.Wire("serial_wr_data", serial_data_width)
    serial_rd_available = m.Wire("serial_rd_available")
    serial_rd_en = m.Wire("serial_rd_en")
    serial_rd_data = m.Wire("serial_rd_data", serial_data_width)
    serial_rd_valid = m.Wire("serial_rd_valid")

    # Protocol Controller In Wires
    m.EmbeddedCode("//Protocol Controller In Wires")
    protocol_controller_in_send_hw_info = m.Wire("send_hw_info")

    # Protocol Controller In Module
    m.EmbeddedCode("//Protocol Controller In Module")
    protocol_controller_in = make_protocol_controller_in(data_width_ext, serial_data_width, hw_info_num_bits)
    con = [("clk", clk), ("rst", rst), ("serial_rd_available", serial_rd_available), ("serial_rd_en", serial_rd_en),
           ("serial_rd_data", serial_rd_data), ("serial_rd_valid", serial_rd_valid), ("rst_afu_out", rst_afus),
           ("start_afu_out", start_afus), ("send_hw_info", protocol_controller_in_send_hw_info),
           ("resp_rd_valid", resp_rd_valid), ("resp_rd_mdata", resp_rd_mdata), ("resp_rd_data", resp_rd_data),
           ("conf", conf), ("conf_valid", conf_valid)]
    params = []
    m.Instance(protocol_controller_in, "protocol_controller_in", params, con)

    # Protocol Controller Out Module
    m.EmbeddedCode("//Protocol Controller Out Module")
    protocol_controller_out = make_protocol_controller_out(data_width_ext, hw_info_num_bits, dms_num_bits,
                                                           serial_data_width)
    con = [("clk", clk), ("rst", rst), ("hw_info", hw_info), ("dsm", dsm),
           ("send_hw_info", protocol_controller_in_send_hw_info), ("serial_wr_available", serial_wr_available),
           ("serial_wr_en", serial_wr_en), ("serial_wr_data", serial_wr_data), ("req_wr_available", req_wr_available),
           ("req_wr_data", req_wr_data), ("req_wr_en", req_wr_en), ("req_rd_en", req_rd_en),
           ("req_rd_mdata", req_rd_mdata), ("req_rd_available", req_rd_available), ("resp_wr_valid", resp_wr_valid),
           ("resp_wr_mdata", resp_wr_mdata)]
    params = []
    m.Instance(protocol_controller_out, "protocol_controller_out", params, con)

    # Serial Module
    m.EmbeddedCode("//Serial Module")
    serial = make_serial(serial_data_width)
    con = [("clk", clk), ("rst", rst), ("rx", rx), ("tx", tx), ("rx_busy", rx_busy),
           ("tx_busy", tx_busy), ("wr_available", serial_wr_available), ("wr_en", serial_wr_en),
           ("wr_data", serial_wr_data), ("rd_available", serial_rd_available), ("rd_en", serial_rd_en),
           ("rd_data", serial_rd_data), ("rd_data_valid", serial_rd_valid)]
    params = []
    m.Instance(serial, "serial", params, con)

    return m


def make_protocol_controller_in(data_width_ext, serial_data_width, hw_info_num_bits):
    m = Module("protocol_controller_in")
    min_bit = data_width_ext - (((data_width_ext // serial_data_width) - 1) * serial_data_width)
    qtde_afu = ((hw_info_num_bits // 8) - 1) // 3

    clk = m.Input("clk")
    rst = m.Input("rst")
    serial_rd_available = m.Input("serial_rd_available")
    serial_rd_en = m.OutputReg("serial_rd_en")
    serial_rd_data = m.Input("serial_rd_data", serial_data_width)
    serial_rd_valid = m.Input("serial_rd_valid")
    send_hw_info = m.OutputReg("send_hw_info")
    resp_rd_valid = m.OutputReg("resp_rd_valid")
    resp_rd_mdata = m.OutputReg("resp_rd_mdata", serial_data_width)
    resp_rd_data = m.OutputReg("resp_rd_data", data_width_ext)
    rst_afu_out = m.Output("rst_afu_out", data_width_ext)
    start_afu_out = m.Output("start_afu_out", data_width_ext)
    conf = m.OutputReg("conf", 40)
    conf_valid = m.OutputReg("conf_valid", 2)

    rst_afu = m.Reg("rst_afu", data_width_ext)
    start_afu = m.Reg("start_afu", data_width_ext)
    start_rst_reg = m.Reg("start_rst_reg", data_width_ext)
    rst_afu_out.assign(rst_afu)
    start_afu_out.assign(start_afu)

    PROTOCOL_HW_INFO = m.Localparam("PROTOCOL_HW_INFO", Int(1, 8, 10))
    PROTOCOL_RST_AFU = m.Localparam("PROTOCOL_RST_AFU", Int(2, 8, 10))
    PROTOCOL_START_AFU = m.Localparam("PROTOCOL_START_AFU", Int(3, 8, 10))
    PROTOCOL_DATA = m.Localparam("PROTOCOL_DATA", Int(4, 8, 10))
    PROTOCOL_CONF_IN = m.Localparam("PROTOCOL_CONF_IN", Int(6, 8, 10))
    PROTOCOL_CONF_OUT = m.Localparam("PROTOCOL_CONF_OUT", Int(7, 8, 10))

    fsm_pctrl = m.Reg("fsm_pctrl", 8)
    FSM_PCTRL_IDLE = m.Localparam("FSM_PCTRL_IDLE", Int(0, fsm_pctrl.width, 10))
    FSM_PCTRL_RD_CODE = m.Localparam("FSM_PCTRL_RD_CODE", Int(1, fsm_pctrl.width, 10))
    FSM_PCTRL_RD_DATA = m.Localparam("FSM_PCTRL_RD_DATA", Int(2, fsm_pctrl.width, 10))
    FSM_PCTRL_DATA = m.Localparam("FSM_PCTRL_DATA", Int(3, fsm_pctrl.width, 10))
    FSM_PCTRL_RD_RST_AFU = m.Localparam("FSM_PCTRL_RD_RST_AFU", Int(4, fsm_pctrl.width, 10))
    FSM_PCTRL_RST_AFU = m.Localparam("FSM_PCTRL_RST_AFU", Int(5, fsm_pctrl.width, 10))
    FSM_PCTRL_RD_START_AFU = m.Localparam("FSM_PCTRL_RD_START_AFU", Int(6, fsm_pctrl.width, 10))
    FSM_PCTRL_START_AFU = m.Localparam("FSM_PCTRL_START_AFU", Int(7, fsm_pctrl.width, 10))
    FSM_PCTRL_RD_CONF_IN = m.Localparam("FSM_PCTRL_RD_CONF_IN", Int(8, fsm_pctrl.width, 10))
    FSM_PCTRL_CONF_IN = m.Localparam("FSM_PCTRL_CONF_IN", Int(9, fsm_pctrl.width, 10))
    FSM_PCTRL_RD_CONF_OUT = m.Localparam("FSM_PCTRL_RD_CONF_OUT", Int(10, fsm_pctrl.width, 10))
    FSM_PCTRL_CONF_OUT = m.Localparam("FSM_PCTRL_CONF_OUT", Int(11, fsm_pctrl.width, 10))

    counter = m.Reg("counter", 8)

    m.Always(Posedge(clk))(
        If(rst)(
            serial_rd_en(Int(0, 1, 2)),
            send_hw_info(Int(0, 1, 2)),
            rst_afu(Int(0, rst_afu.width, 2)),
            start_afu(Int(0, start_afu.width, 2)),
            counter(Int(0, counter.width, 10)),
            resp_rd_valid(Int(0, 1, 2)),
            conf_valid(Int(0, 2, 2)),
            fsm_pctrl(FSM_PCTRL_IDLE),
        ).Else(
            serial_rd_en(Int(0, 1, 2)),
            resp_rd_valid(Int(0, 1, 2)),
            conf_valid(Int(0, 2, 2)),
            Case(fsm_pctrl)(
                When(FSM_PCTRL_IDLE)(
                    If(serial_rd_available)(
                        serial_rd_en(Int(1, 1, 2)),
                        fsm_pctrl(FSM_PCTRL_RD_CODE),
                    )
                ),
                When(FSM_PCTRL_RD_DATA)(
                    fsm_pctrl(FSM_PCTRL_RD_DATA),
                    If(serial_rd_available)(
                        fsm_pctrl(FSM_PCTRL_DATA),
                        serial_rd_en(Int(1, 1, 2)),
                    )
                ),
                When(FSM_PCTRL_DATA)(
                    If(serial_rd_valid)(
                        fsm_pctrl(FSM_PCTRL_RD_DATA),
                        counter(counter + Int(1, counter.width, 10)),
                        If(counter == Int(0, counter.width, 10))(
                            resp_rd_mdata(serial_rd_data),
                        ).Elif(counter < Int(4, counter.width, 10))(
                            resp_rd_data(Cat(serial_rd_data, resp_rd_data[min_bit:data_width_ext])),
                        ).Else(
                            resp_rd_data(Cat(serial_rd_data, resp_rd_data[min_bit:data_width_ext])),
                            resp_rd_valid(Int(1, 1, 2)),
                            fsm_pctrl(FSM_PCTRL_IDLE),
                        ),
                    )
                ),
                When(FSM_PCTRL_RD_RST_AFU)(
                    fsm_pctrl(FSM_PCTRL_RD_RST_AFU),
                    If(serial_rd_available)(
                        fsm_pctrl(FSM_PCTRL_RST_AFU),
                        serial_rd_en(Int(1, 1, 2)),
                    )
                ),
                When(FSM_PCTRL_RST_AFU)(
                    If(serial_rd_valid)(
                        fsm_pctrl(FSM_PCTRL_RD_RST_AFU),
                        counter(counter + Int(1, counter.width, 10)),
                        If(counter < Int(3, counter.width, 10))(
                            start_rst_reg(Cat(serial_rd_data, start_rst_reg[min_bit:data_width_ext])),
                        ).Else(
                            rst_afu(Cat(serial_rd_data, start_rst_reg[min_bit:data_width_ext])),
                            fsm_pctrl(FSM_PCTRL_IDLE),
                        ),
                    )
                ),
                When(FSM_PCTRL_RD_START_AFU)(
                    fsm_pctrl(FSM_PCTRL_RD_START_AFU),
                    If(serial_rd_available)(
                        fsm_pctrl(FSM_PCTRL_START_AFU),
                        serial_rd_en(Int(1, 1, 2)),
                    )
                ),
                When(FSM_PCTRL_START_AFU)(
                    If(serial_rd_valid)(
                        fsm_pctrl(FSM_PCTRL_RD_START_AFU),
                        counter(counter + Int(1, counter.width, 10)),
                        If(counter < Int(3, counter.width, 10))(
                            start_rst_reg(Cat(serial_rd_data, start_rst_reg[min_bit:data_width_ext])),
                        ).Else(
                            start_afu(Cat(serial_rd_data, start_rst_reg[min_bit:data_width_ext])),
                            fsm_pctrl(FSM_PCTRL_IDLE),
                        ),
                    )
                ),
                When(FSM_PCTRL_RD_CONF_IN)(
                    fsm_pctrl(FSM_PCTRL_RD_CONF_IN),
                    If(serial_rd_available)(
                        fsm_pctrl(FSM_PCTRL_CONF_IN),
                        serial_rd_en(Int(1, 1, 2)),
                    )
                ),
                When(FSM_PCTRL_CONF_IN)(
                    If(serial_rd_valid)(
                        fsm_pctrl(FSM_PCTRL_RD_CONF_IN),
                        counter(counter + Int(1, counter.width, 10)),
                        If(counter < Int(4, counter.width, 10))(
                            conf(Cat(serial_rd_data, conf[min_bit:40])),
                        ).Else(
                            conf(Cat(serial_rd_data, conf[min_bit:40])),
                            conf_valid(Int(1, 2, 2)),
                            fsm_pctrl(FSM_PCTRL_IDLE),
                        ),
                    )
                ),
                When(FSM_PCTRL_RD_CONF_OUT)(
                    fsm_pctrl(FSM_PCTRL_RD_CONF_OUT),
                    If(serial_rd_available)(
                        fsm_pctrl(FSM_PCTRL_CONF_OUT),
                        serial_rd_en(Int(1, 1, 2)),
                    )
                ),
                When(FSM_PCTRL_CONF_OUT)(
                    If(serial_rd_valid)(
                        fsm_pctrl(FSM_PCTRL_RD_CONF_OUT),
                        counter(counter + Int(1, counter.width, 10)),
                        If(counter < Int(4, counter.width, 10))(
                            conf(Cat(serial_rd_data, conf[min_bit:40])),
                        ).Else(
                            conf(Cat(serial_rd_data, conf[min_bit:40])),
                            conf_valid(Int(2, 2, 2)),
                            fsm_pctrl(FSM_PCTRL_IDLE),
                        ),
                    )
                ),
                When(FSM_PCTRL_RD_CODE)(
                    If(serial_rd_valid)(
                        counter(Int(0, counter.width, 10)),
                        Case(serial_rd_data)(
                            When(PROTOCOL_HW_INFO)(
                                send_hw_info(~send_hw_info),
                                fsm_pctrl(FSM_PCTRL_IDLE),
                            ),
                            When(PROTOCOL_RST_AFU)(
                                fsm_pctrl(FSM_PCTRL_RD_RST_AFU),
                            ),
                            When(PROTOCOL_START_AFU)(
                                fsm_pctrl(FSM_PCTRL_RD_START_AFU),
                            ),
                            When(PROTOCOL_DATA)(
                                fsm_pctrl(FSM_PCTRL_RD_DATA),
                            ),
                            When(PROTOCOL_CONF_IN)(
                                fsm_pctrl(FSM_PCTRL_RD_CONF_IN),
                            ),
                            When(PROTOCOL_CONF_OUT)(
                                fsm_pctrl(FSM_PCTRL_RD_CONF_OUT),
                            ),
                        )
                    )
                ),
            )
        )
    )

    return m


def make_protocol_controller_out(data_width_ext, hw_info_num_bits, dms_num_bits, serial_data_width):
    m = Module("protocol_controller_out")

    clk = m.Input("clk")
    rst = m.Input("rst")
    hw_info = m.Input("hw_info", hw_info_num_bits)
    dsm = m.Input("dsm", dms_num_bits)
    send_hw_info = m.Input("send_hw_info")

    serial_wr_available = m.Input("serial_wr_available")
    serial_wr_en = m.OutputReg("serial_wr_en")
    serial_wr_data = m.OutputReg("serial_wr_data", serial_data_width)

    req_rd_available = m.Output("req_rd_available")
    req_rd_en = m.Input("req_rd_en")
    req_rd_mdata = m.Input("req_rd_mdata", serial_data_width)

    resp_wr_valid = m.OutputReg("resp_wr_valid")
    resp_wr_mdata = m.OutputReg("resp_wr_mdata", serial_data_width)

    req_wr_en = m.Input("req_wr_en")
    req_wr_available = m.Output("req_wr_available")
    req_wr_data = m.Input("req_wr_data", data_width_ext + serial_data_width)

    dsm_reg = m.Reg("dsm_reg", dms_num_bits)
    send_dsm = m.Reg("send_dsm")
    sent_dsm = m.Reg("sent_dsm")
    # fifo_data Wires
    m.EmbeddedCode("//Fifo Wires")
    fifo_data_valid = m.Wire("fifo_data_valid")
    fifo_data_rd_en = m.Reg("fifo_data_rd_en")
    fifo_data_dout = m.Wire("fifo_data_dout", data_width_ext + serial_data_width)
    fifo_data_empty = m.Wire("fifo_data_empty")
    fifo_data_almost_empty = m.Wire("fifo_data_almost_empty")
    fifo_data_full = m.Wire("fifo_data_full")
    fifo_data_almost_full = m.Wire("fifo_data_almost_full")
    fifo_data_count = m.Wire("fifo_data_count", 3)
    req_wr_available.assign(~fifo_data_almost_full)

    m.EmbeddedCode("//fifo_data")
    fifo = make_fifo()
    # FIFO_DATA
    con = [("clk", clk), ("rst", rst), ("we", req_wr_en), ("din", req_wr_data), ("re", fifo_data_rd_en),
           ("dout", fifo_data_dout), ("empty", fifo_data_empty), ("almostempty", fifo_data_almost_empty),
           ("full", fifo_data_full), ("almostfull", fifo_data_almost_full), ("count", fifo_data_count),
           ("valid", fifo_data_valid)]
    params = [("FIFO_WIDTH", data_width_ext + serial_data_width), ("FIFO_DEPTH_BITS", 3)]
    m.Instance(fifo, "fifo_data", params, con)

    # fifo_data Wires
    m.EmbeddedCode("//Fifo Wires")
    fifo_req_valid = m.Wire("fifo_req_valid")
    fifo_req_rd_en = m.Reg("fifo_req_rd_en")
    fifo_req_dout = m.Wire("fifo_req_dout", serial_data_width)
    fifo_req_empty = m.Wire("fifo_req_empty")
    fifo_req_almost_empty = m.Wire("fifo_req_almost_empty")
    fifo_req_full = m.Wire("fifo_req_full")
    fifo_req_almost_full = m.Wire("fifo_req_almost_full")
    fifo_req_count = m.Wire("fifo_req_count", 3)
    req_rd_available.assign(~fifo_req_almost_full)

    # FIFO_REQ
    con = [("clk", clk), ("rst", rst), ("we", req_rd_en), ("din", req_rd_mdata), ("re", fifo_req_rd_en),
           ("dout", fifo_req_dout), ("empty", fifo_req_empty), ("almostempty", fifo_req_almost_empty),
           ("full", fifo_req_full), ("almostfull", fifo_req_almost_full), ("count", fifo_req_count),
           ("valid", fifo_req_valid)]
    params = [("FIFO_WIDTH", serial_data_width), ("FIFO_DEPTH_BITS", 3)]
    m.Instance(fifo, "fifo_req", params, con)

    fsm_pctrl = m.Reg("fsm_pctrl", 4)
    FSM_PCTRL_IDLE = m.Localparam("FSM_PCTRL_IDLE", Int(0, fsm_pctrl.width, 2))
    FSM_PCTRL_SEND_HW_INFO = m.Localparam("FSM_PCTRL_SEND_HW_INFO", Int(1, fsm_pctrl.width, 2))
    FSM_PCTRL_SEND_DSM = m.Localparam("FSM_PCTRL_SEND_DSM", Int(2, fsm_pctrl.width, 2))
    FSM_PCTRL_SEND_DATA1 = m.Localparam("FSM_PCTRL_SEND_DATA1", Int(3, fsm_pctrl.width, 2))
    FSM_PCTRL_SEND_DATA2 = m.Localparam("FSM_PCTRL_SEND_DATA2", Int(4, fsm_pctrl.width, 2))
    FSM_PCTRL_SEND_REQ1 = m.Localparam("FSM_PCTRL_SEND_REQ1", Int(5, fsm_pctrl.width, 2))
    FSM_PCTRL_SEND_REQ2 = m.Localparam("FSM_PCTRL_SEND_REQ2", Int(6, fsm_pctrl.width, 2))

    counter = m.Reg("counter", ceil(log2(hw_info_num_bits)))
    sent_hw_info = m.Reg("sent_hw_info")
    data = m.Reg("data", data_width_ext + serial_data_width)

    m.Always(Posedge(clk))(
        If(rst)(
            sent_hw_info(Int(0, 1, 2)),
            sent_dsm(Int(0, 1, 2)),
            serial_wr_en(Int(0, 1, 2)),
            fifo_data_rd_en(Int(0, 1, 2)),
            fifo_req_rd_en(Int(0, 1, 2)),
            resp_wr_valid(Int(0, 1, 2)),
            fsm_pctrl(FSM_PCTRL_IDLE),
        ).Else(
            serial_wr_en(Int(0, 1, 2)),
            fifo_data_rd_en(Int(0, 1, 2)),
            fifo_req_rd_en(Int(0, 1, 2)),
            resp_wr_valid(Int(0, 1, 2)),
            Case(fsm_pctrl)(
                When(FSM_PCTRL_IDLE)(
                    If(send_hw_info != sent_hw_info)(
                        sent_hw_info(send_hw_info),
                        counter(Int(0, counter.width, 10)),
                        fsm_pctrl(FSM_PCTRL_SEND_HW_INFO)
                    ).Elif(sent_dsm != send_dsm)(
                        counter(Int(0, counter.width, 10)),
                        fsm_pctrl(FSM_PCTRL_SEND_DSM)
                    ).Elif(Not(fifo_data_empty))(
                        fifo_data_rd_en(Int(1, 1, 2)),
                        fsm_pctrl(FSM_PCTRL_SEND_DATA1),
                    ).Elif(Not(fifo_req_empty))(
                        fsm_pctrl(FSM_PCTRL_SEND_REQ1),
                    )
                ),
                When(FSM_PCTRL_SEND_DATA1)(
                    If(fifo_data_valid)(
                        fsm_pctrl(FSM_PCTRL_SEND_DATA2),
                        data(fifo_data_dout),
                        counter(Int(0, counter.width, 10)),
                        resp_wr_mdata(fifo_data_dout[0:8]),
                    )
                ),
                When(FSM_PCTRL_SEND_DATA2)(
                    fsm_pctrl(FSM_PCTRL_SEND_DATA2),
                    If(serial_wr_available)(
                        If(counter == Int(0, counter.width, 10))(
                            serial_wr_data(Int(4, serial_data_width, 10)),
                            serial_wr_en(Int(1, 1, 2)),
                            counter(counter + Int(1, counter.width, 10)),
                        ).Elif(counter < Int(6, counter.width, 10))(
                            data(Cat(Int(0, serial_data_width, 10), data[8:(data_width_ext + serial_data_width)])),
                            serial_wr_data(data[0:serial_data_width]),
                            serial_wr_en(Int(1, 1, 2)),
                            counter(counter + Int(1, counter.width, 10)),
                        ).Else(
                            resp_wr_valid(Int(1, 1, 2)),
                            If(Not(fifo_req_empty))(
                                fsm_pctrl(FSM_PCTRL_SEND_REQ1),
                            ),
                            fsm_pctrl(FSM_PCTRL_IDLE),
                        )
                    ),
                ),
                When(FSM_PCTRL_SEND_REQ1)(
                    fsm_pctrl(FSM_PCTRL_SEND_REQ1),
                    If(serial_wr_available)(
                        serial_wr_data(Int(5, serial_data_width, 10)),
                        serial_wr_en(Int(1, 1, 2)),
                        fifo_req_rd_en(Int(1, 1, 2)),
                        fsm_pctrl(FSM_PCTRL_SEND_REQ2),
                    ),
                ),
                When(FSM_PCTRL_SEND_REQ2)(
                    If(fifo_req_valid)(
                        serial_wr_data(fifo_req_dout),
                        serial_wr_en(Int(1, 1, 2)),
                        fsm_pctrl(FSM_PCTRL_IDLE),
                    )
                ),
                When(FSM_PCTRL_SEND_HW_INFO)(
                    fsm_pctrl(FSM_PCTRL_SEND_HW_INFO),
                    If(serial_wr_available)(
                        counter(counter + Int(1, counter.width, 10)),
                        serial_wr_en(Int(1, 1, 2)),
                        If(counter == Int(0, counter.width, 10))(
                            serial_wr_data(Int(1, serial_data_width, 10)),
                        ).Elif(counter < Int((hw_info_num_bits // 8), counter.width, 10))(
                            serial_wr_data(
                                hw_info >> ((counter - Int(1, counter.width, 10)) * Int(8, counter.width, 10))),
                        ).Else(
                            serial_wr_data(
                                hw_info >> ((counter - Int(1, counter.width, 10)) * Int(8, counter.width, 10))),
                            fsm_pctrl(FSM_PCTRL_IDLE),
                        )
                    )
                ),
                When(FSM_PCTRL_SEND_DSM)(
                    fsm_pctrl(FSM_PCTRL_SEND_DSM),
                    If(serial_wr_available)(
                        counter(counter + Int(1, counter.width, 10)),
                        serial_wr_en(Int(1, 1, 2)),
                        If(counter == Int(0, counter.width, 10))(
                            serial_wr_data(Int(8, serial_data_width, 10)),
                        ).Elif(counter < Int((dms_num_bits // 8), counter.width, 10))(
                            serial_wr_data(
                                dsm_reg >> ((counter - Int(1, counter.width, 10)) * Int(8, counter.width, 10))),
                        ).Else(
                            serial_wr_data(
                                dsm_reg >> ((counter - Int(1, counter.width, 10)) * Int(8, counter.width, 10))),
                            sent_dsm(send_dsm),
                            fsm_pctrl(FSM_PCTRL_IDLE),
                        )
                    )
                )
            )
        )
    )

    m.Always(Posedge(clk))(
        If(rst)(
            send_dsm(Int(0, 1, 2)),
            dsm_reg(dsm),
        ).Elif(AndList(dsm != dsm_reg, send_dsm == sent_dsm))(
            dsm_reg(dsm),
            send_dsm(~send_dsm),
        )
    )

    return m
