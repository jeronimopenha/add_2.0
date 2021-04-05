from veriloggen import *

from make_fifo_input_controller import make_fifo_input_controller
from make_fifo_output_controller import make_fifo_output_controller


def make_afu(data_width_ext, serial_data_width, mdata_in_ini, mdata_out_ini, afu_number, afu_info,afu_user):
    m = Module("afu%d" % afu_number)

    qtde_input = 0
    qtde_output = 0

    for afu in afu_info:
        qtde_input = qtde_input + afu[0]
        qtde_output = qtde_output + afu[1]

    # basic Signals
    clk = m.Input("clk")
    rst = m.Input("rst")
    start = m.Input("start")
    conf = m.Input("conf", 40)
    conf_valid = m.Input("conf_valid", 2)

    # Communication pins
    resp_rd_valid = m.Input("resp_rd_valid")
    resp_rd_mdata = m.Input("resp_rd_mdata", serial_data_width)
    resp_rd_data = m.Input("resp_rd_data", data_width_ext)

    resp_wr_valid = m.Input("resp_wr_valid")
    resp_wr_mdata = m.Input("resp_wr_mdata", serial_data_width)

    req_rd_en = m.Output("req_rd_en", qtde_input)
    req_rd_mdata = m.Output("req_rd_mdata", qtde_input * serial_data_width)
    req_rd_available = m.Input("req_rd_available", qtde_input)

    req_wr_en = m.Output("req_wr_en", qtde_output)
    req_wr_available = m.Input("req_wr_available", qtde_output)
    req_wr_mdata = m.Output("req_wr_mdata", qtde_output * serial_data_width)
    req_wr_data = m.Output("req_wr_data", qtde_output * data_width_ext)

    # DSM Pins
    dsm = m.OutputReg("dsm", qtde_output + qtde_input + 1)


    # done wires
    m.EmbeddedCode("//Done_wires")
    rd_done = m.Wire("rd_done", qtde_input)
    wr_done = m.Wire("wr_done", qtde_output)
    afu_user_done = m.Wire("afu_user_done")

    # Other wires
    m.EmbeddedCode("//Other wires")
    input_controllers_has_pending = m.Wire("input_controllers_has_pending", qtde_input)
    output_controllers_has_pending = m.Wire("output_controllers_has_pending", qtde_output)
    afu_user_rd_available = m.Wire("afu_user_rd_available", qtde_input)
    afu_user_rd_data = m.Wire("afu_user_rd_data", data_width_ext * qtde_input)
    afu_user_rd_en = m.Wire("afu_user_rd_en", qtde_input)
    afu_user_rd_valid = m.Wire("afu_user_rd_valid", qtde_input)
    afu_user_wr_available = m.Wire("afu_user_wr_available", qtde_output)
    afu_user_wr_data = m.Wire("afu_user_wr_data", data_width_ext * qtde_output)
    afu_user_wr_en = m.Wire("afu_user_wr_en", qtde_output)

    # AFU DMS controller
    m.EmbeddedCode("//AFU DMS controller")
    m.Always(Posedge(clk))(
        If(rst)(
            dsm(Int(0, dsm.width, 10)),
        ).Elif(start)(
            dsm(Cat(wr_done, rd_done, afu_user_done)),
        )
    )

    in_controller = make_fifo_input_controller(data_width_ext, serial_data_width)
    for i in range(0, qtde_input):
        con = [("clk", clk), ("rst", rst), ("start", start), ("conf", conf), ("conf_valid", conf_valid),
               ("has_pending", input_controllers_has_pending[i]), ("req_rd_available", req_rd_available[i]),
               ("req_rd_mdata", req_rd_mdata[i * serial_data_width:(i * serial_data_width) + serial_data_width]),
               ("req_rd_en", req_rd_en[i]), ("resp_rd_valid", resp_rd_valid), ("resp_rd_data", resp_rd_data),
               ("resp_rd_mdata", resp_rd_mdata), ("afu_user_rd_available", afu_user_rd_available[i]),
               ("afu_user_rd_data", afu_user_rd_data[i * data_width_ext:(i * data_width_ext) + data_width_ext]),
               ("afu_user_rd_en", afu_user_rd_en[i]), ("afu_user_rd_valid", afu_user_rd_valid[i]), ("rd_done", rd_done[i])]
        params = [("CONTROL_MDATA", mdata_in_ini + i)]
        m.Instance(in_controller, "in_controller%d" % i, params, con)

    out_controller = make_fifo_output_controller(data_width_ext, serial_data_width)
    for i in range(0, qtde_output):
        con = [("clk", clk), ("rst", rst), ("start", start), ("conf", conf), ("conf_valid", conf_valid),
               ("has_wr_pending", output_controllers_has_pending[i]), ("afu_user_done", afu_user_done),
               ("req_wr_available", req_wr_available[i]),
               ("req_wr_mdata", req_wr_mdata[i * serial_data_width:(i * serial_data_width) + serial_data_width]),
               ("req_wr_data", req_wr_data[i * data_width_ext:(i * data_width_ext) + data_width_ext]),
               ("req_wr_en", req_wr_en[i]), ("resp_wr_valid", resp_wr_valid), ("resp_wr_mdata", resp_wr_mdata),
               ("afu_user_wr_available", afu_user_wr_available[i]),
               ("afu_user_wr_data", afu_user_wr_data[i * data_width_ext:(i * data_width_ext) + data_width_ext]),
               ("afu_user_wr_en", afu_user_wr_en[i]), ("wr_done", wr_done[i])]
        params = [("CONTROL_MDATA", mdata_out_ini + i)]
        m.Instance(out_controller, "out_controller%d" % i, params, con)

    #afu_user = make_afu_user(data_width_ext, 0)
    con = [("clk", clk), ("rst", rst), ("start", start), ("afu_user_done_rd_data", rd_done), ("afu_user_done_wr_data", wr_done),
           ("afu_user_available_read", afu_user_rd_available), ("afu_user_read_data", afu_user_rd_data),
           ("afu_user_request_read", afu_user_rd_en), ("afu_user_read_data_valid", afu_user_rd_valid),
           ("afu_user_available_write", afu_user_wr_available), ("afu_user_write_data", afu_user_wr_data),
           ("afu_user_request_write", afu_user_wr_en), ("afu_user_done", afu_user_done)]
    params = []
    m.Instance(afu_user, "afu_user%d" % 0, params, con)

    return m


#make_afu(32, 8, 3, 2, 0, [(1, 1)]).to_verilog('verilog')
