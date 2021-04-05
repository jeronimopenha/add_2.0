from veriloggen import *

from make_afu import make_afu
from make_fifo import make_fifo
from make_fifo_selector import make_fifo_selector
from util import *


def make_afu_manager(data_width_ext, serial_data_width, total_input, total_output, afus_user_str, afus_user_mod):
    m = Module("afu_manager")


    total_afu = len(afus_user_mod)
    input_afus = []
    output_afus = []

    for afu_user_str in afus_user_str:
        input_afus.append(get_afu_num_in(afu_user_str))
        output_afus.append(get_afu_num_out(afu_user_str))



    # basic Signals
    clk = m.Input("clk")
    rst = m.Input("rst")
    dsm = m.Output("dsm", total_afu + total_input + total_output)
    conf = m.Input("conf", 40)
    conf_valid = m.Input("conf_valid", 2)

    rst_afus = m.Input("rst_afus", total_afu)
    start_afus = m.Input("start_afus", total_afu)

    # Communication pins
    resp_rd_valid = m.Input("resp_rd_valid")
    resp_rd_mdata = m.Input("resp_rd_mdata", serial_data_width)
    resp_rd_data = m.Input("resp_rd_data", data_width_ext)

    resp_wr_valid = m.Input("resp_wr_valid")
    resp_wr_mdata = m.Input("resp_wr_mdata", serial_data_width)

    req_rd_en = m.Output("req_rd_en")
    req_rd_mdata = m.Output("req_rd_mdata", serial_data_width)
    req_rd_available = m.Input("req_rd_available")

    req_wr_en = m.Output("req_wr_en")
    req_wr_available = m.Input("req_wr_available")
    req_wr_data = m.Output("req_wr_data",(data_width_ext + serial_data_width))

    # AFU_Wires
    m.EmbeddedCode("//AFU_Wires")
    afu_req_rd_en = m.Wire("afu_req_rd_en", total_input)
    afu_req_rd_mdata = m.Wire("afu_req_rd_mdata", total_input * serial_data_width)
    afu_req_rd_available = m.Wire("afu_req_rd_available", total_input)
    afu_req_wr_en = m.Wire("afu_req_wr_en", total_output)
    afu_req_wr_available = m.Wire("afu_req_wr_available", total_output)
    afu_req_wr_mdata = m.Wire("afu_req_wr_mdata", total_output * serial_data_width)
    afu_req_wr_data = m.Wire("afu_req_wr_data", total_output * data_width_ext)

    # fifo_mdata_Wires
    m.EmbeddedCode("//fifo_mdata_Wires")
    fifo_mdata_re = m.Wire("fifo_mdata_re", total_input)
    fifo_mdata_dout = m.Wire("fifo_mdata_dout", total_input * serial_data_width)
    fifo_mdata_empty = m.Wire("fifo_mdata_empty", total_input)
    fifo_mdata_almost_empty = m.Wire("fifo_mdata_almost_empty", total_input)
    fifo_mdata_full = m.Wire("fifo_mdata_full", total_input)
    fifo_mdata_almost_full = m.Wire("fifo_mdata_almost_full", total_input)
    fifo_mdata_count = m.Wire("fifo_mdata_count", data_width_ext, total_input)
    fifo_mdata_valid = m.Wire("fifo_mdata_valid", total_input)

    # fifo_wrdata_Wires
    m.EmbeddedCode("//fifo_wrdata_Wires")
    fifo_wrdata_re = m.Wire("fifo_wrdata_re", total_output)
    fifo_wrdata_dout = m.Wire("fifo_wrdata_dout", total_output * (serial_data_width + data_width_ext))
    fifo_wrdata_empty = m.Wire("fifo_wrdata_empty", total_output)
    fifo_wrdata_almost_empty = m.Wire("fifo_wrdata_almost_empty", total_output)
    fifo_wrdata_full = m.Wire("fifo_wrdata_full", total_output)
    fifo_wrdata_almost_full = m.Wire("fifo_wrdata_almost_full", total_output)
    fifo_wrdata_count = m.Wire("fifo_wrdata_count", data_width_ext, total_output)
    fifo_wrdata_valid = m.Wire("fifo_wrdata_valid", total_output)

    for i in range (0,total_input):
        afu_req_rd_available[i].assign(~fifo_mdata_almost_full[i])

    for i in range (0,total_output):
        afu_req_wr_available[i].assign(~fifo_wrdata_almost_full[i])

    # Temporary AFU
    mdata_in_ini = 0
    mdata_out_ini = 0
    acc_input = 0
    acc_output = 0
    acc_dms = 0
    i = 0
    for afu_user_mod in afus_user_mod:
        afu_info = [(input_afus[i], output_afus[i])]
        afu = make_afu(data_width_ext, serial_data_width, acc_input, acc_output, i, afu_info, afu_user_mod)
        con = [("clk", clk), ("rst", rst_afus[i]), ("start", start_afus[i]), ("conf", conf), ("conf_valid", conf_valid),
               ("resp_rd_valid", resp_rd_valid), ("resp_rd_mdata", resp_rd_mdata), ("resp_rd_data", resp_rd_data),
               ("resp_wr_valid", resp_wr_valid), ("resp_wr_mdata", resp_wr_mdata),
               ("req_rd_en", afu_req_rd_en[acc_input:input_afus[i] + acc_input]),
               ("req_rd_mdata",
                afu_req_rd_mdata[acc_input * serial_data_width:(acc_input + input_afus[i]) * serial_data_width]),
               ("req_rd_available", afu_req_rd_available[acc_input:input_afus[i] + acc_input]),
               ("req_wr_en", afu_req_wr_en[acc_output:output_afus[i] + acc_output]),
               ("req_wr_available", afu_req_wr_available[acc_output:output_afus[i] + acc_output]),
               ("req_wr_mdata",
                afu_req_wr_mdata[acc_output * serial_data_width:(acc_output + output_afus[i]) * serial_data_width]),
               ("req_wr_data",
                afu_req_wr_data[acc_output * data_width_ext:(acc_output + output_afus[i]) * data_width_ext]),
               ("dsm", dsm[acc_dms:acc_dms + input_afus[i] + output_afus[i] + 1])]
        params = []
        m.Instance(afu, "afu%d" % i, params, con)

        acc_input = acc_input + input_afus[i]
        acc_output = acc_output + output_afus[i]
        acc_dms = acc_dms + input_afus[i] + output_afus[i] + 1
        i = i + 1

    fifo = make_fifo()
    for i in range(0, total_input):
        con = [("clk", clk), ("rst", rst), ("we", afu_req_rd_en[i]),
               ("din", afu_req_rd_mdata[i * serial_data_width:(i * serial_data_width) + serial_data_width]),
               ("re", fifo_mdata_re[i]),
               ("dout", fifo_mdata_dout[i * serial_data_width:(i * serial_data_width) + serial_data_width]),
               ("empty", fifo_mdata_empty[i]), ("almostempty", fifo_mdata_almost_empty[i]),
               ("full", fifo_mdata_full[i]), ("almostfull", fifo_mdata_almost_full[i]), ("count", fifo_mdata_count[i]),
               ("valid", fifo_mdata_valid[i])]
        params = [("FIFO_WIDTH", serial_data_width), ("FIFO_DEPTH_BITS", 3)]
        m.Instance(fifo, "fifo_mdata%d" % i, params, con)

    for i in range(0, total_output):
        con = [("clk", clk), ("rst", rst), ("we", afu_req_wr_en[i]),
               ("din", Cat(afu_req_wr_data[i * data_width_ext:(i * data_width_ext) + data_width_ext],
                           afu_req_wr_mdata[i * serial_data_width:(i * serial_data_width) + serial_data_width])),
               ("re", fifo_wrdata_re[i]),
               ("dout", fifo_wrdata_dout[
                        i * (serial_data_width + data_width_ext):(i * (serial_data_width + data_width_ext)) + (
                            serial_data_width + data_width_ext)]),
               ("empty", fifo_wrdata_empty[i]), ("almostempty", fifo_wrdata_almost_empty[i]),
               ("full", fifo_wrdata_full[i]), ("almostfull", fifo_wrdata_almost_full[i]),
               ("count", fifo_wrdata_count[i]),
               ("valid", fifo_wrdata_valid[i])]
        params = [("FIFO_WIDTH", serial_data_width + data_width_ext), ("FIFO_DEPTH_BITS", 3)]
        m.Instance(fifo, "fifo_wrdata%d" % i, params, con)

    fifo_selector_mdata = make_fifo_selector(serial_data_width, total_input)
    con = [("clk", clk), ("rst", rst), ("data_in_re", fifo_mdata_re), ("data_in_data", fifo_mdata_dout),
           ("data_in_available", ~fifo_mdata_empty), ("data_in_valid", fifo_mdata_valid), ("req_wr_en", req_rd_en),
           ("req_wr_data", req_rd_mdata), ("req_wr_available", req_rd_available)]
    params = []
    m.Instance(fifo_selector_mdata, "fifo_selector_mdata", params, con)

    fifo_selector_data = make_fifo_selector(data_width_ext + serial_data_width, total_input)
    con = [("clk", clk), ("rst", rst), ("data_in_re", fifo_wrdata_re), ("data_in_data", fifo_wrdata_dout),
           ("data_in_available", ~fifo_wrdata_empty), ("data_in_valid", fifo_wrdata_valid), ("req_wr_en", req_wr_en),
           ("req_wr_data", req_wr_data), ("req_wr_available", req_wr_available)]
    params = []
    m.Instance(fifo_selector_data, "fifo_selector_data", params, con)

    return m
