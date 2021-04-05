from veriloggen import *

from make_afu_manager import make_afu_manager
from make_afu_user import make_afus_user
from make_communication_controller import make_communication_controller
from util import *


def make_add_hw(data_width_ext, serial_data_width, hds, board):
    # obter o conteúdo do arquivo hds
    hds_string = get_hds_string(hds)

    # obter nome do dfg
    dfg_name = get_dfg_name(hds_string)

    # limpar a string do hds
    hds_string = clean_hds(hds_string)

    # obter os sinais do dfg
    signals = get_dfg_signals(hds_string)

    # obter as AFUs com seus componentes
    afus_user_str = get_dfg_afus(hds_string)
    afus_user_str.sort()

    afus_user_mod = make_afus_user(afus_user_str, signals, data_width_ext)

    tot_input = 0
    tot_output = 0
    tot_afu = len(afus_user_str)

    for afu_user_str in afus_user_str:
        tot_input = tot_input + get_afu_num_in(afu_user_str)
        tot_output = tot_output + get_afu_num_out(afu_user_str)

    hw_info_num_bits = (len(afus_user_str) * 3 * 8) + 8
    tam_dms_num_bits = tot_input + tot_afu + tot_output

    m = Module("add_hw")

    clk = m.Input("clk")
    rst = m.Input("rst")
    uartrxd = m.Input("uartrxd")
    uarttxd = m.Output("uarttxd")
    led_rx_busy = m.Output("led_rx_busy")
    led_tx_busy = m.Output("led_tx_busy")
    led_rst = m.Output("led_rst")
    led_start = m.Output("led_start")
    if board == "mercurioiv":
        led_base = m.Output("led_base")

    # Wires for AFU
    m.EmbeddedCode("//Wires for AFU")
    hw_info = m.Wire("hw_info", hw_info_num_bits)
    rst_afus = m.Wire("rst_afus", data_width_ext)
    start_afus = m.Wire("start_afus", data_width_ext)

    resp_rd_valid = m.Wire("resp_rd_valid")
    resp_rd_mdata = m.Wire("resp_rd_mdata", serial_data_width)
    resp_rd_data = m.Wire("resp_rd_data", data_width_ext)

    req_rd_available = m.Wire("req_rd_available")
    req_rd_en = m.Wire("req_rd_en")
    req_rd_mdata = m.Wire("req_rd_mdata", serial_data_width)

    resp_wr_valid = m.Wire("resp_wr_valid")
    resp_wr_mdata = m.Wire("resp_wr_mdata", serial_data_width)

    req_wr_en = m.Wire("req_wr_en")
    req_wr_available = m.Wire("req_wr_available")
    req_wr_data = m.Wire("req_wr_data", data_width_ext + serial_data_width)

    dsm = m.Wire("dsm", tam_dms_num_bits)
    conf = m.Wire("conf", 40)
    conf_valid = m.Wire("conf_valid", 2)

    # Led Assignments
    m.EmbeddedCode("//Led Assignments")

    if board == "mercurioiv":
        led_base.assign(Int(0, 1, 2))
        led_rst.assign(~rst_afus[0])
        led_start.assign(~start_afus[0])


    m.EmbeddedCode("//communication_controller module")
    communication_controller = make_communication_controller(data_width_ext, hw_info_num_bits, tam_dms_num_bits,
                                                             serial_data_width)
    con = [("clk", clk), ("rst", rst), ("rx", uartrxd), ("tx", uarttxd), ("rx_busy", led_rx_busy),
           ("tx_busy", led_tx_busy), ("rst_afus", rst_afus), ("start_afus", start_afus), ("hw_info", hw_info),
           ("resp_rd_valid", resp_rd_valid), ("resp_rd_mdata", resp_rd_mdata), ("resp_rd_data", resp_rd_data),
           ("req_wr_en", req_wr_en), ("req_wr_available", req_wr_available), ("req_wr_data", req_wr_data),
           ("req_rd_en", req_rd_en), ("req_rd_mdata", req_rd_mdata), ("req_rd_available", req_rd_available),
           ("conf", conf), ("conf_valid", conf_valid), ("resp_wr_mdata", resp_wr_mdata),
           ("resp_wr_valid", resp_wr_valid), ("dsm", dsm)]
    params = []

    m.Instance(communication_controller, "communication_controller", params, con)

    m.EmbeddedCode("//AFUs_Manager")

    afu_manager = make_afu_manager(data_width_ext, serial_data_width, tot_input, tot_output, afus_user_str,
                                   afus_user_mod)

    con = [("clk", clk), ("rst", rst), ("dsm", dsm), ("conf", conf), ("conf_valid", conf_valid), ("rst_afus", rst_afus),
           ("start_afus", start_afus), ("resp_rd_valid", resp_rd_valid), ("resp_rd_mdata", resp_rd_mdata),
           ("resp_rd_data", resp_rd_data), ("resp_wr_valid", resp_wr_valid), ("resp_wr_mdata", resp_wr_mdata),
           ("req_rd_en", req_rd_en), ("req_rd_mdata", req_rd_mdata),
           ("req_rd_available", req_rd_available), ("req_wr_en", req_wr_en), ("req_wr_available", req_wr_available),
           ("req_wr_data", req_wr_data)]
    params = []
    m.Instance(afu_manager, "afu_manager", params, con)

    hw_info_str = ""
    for afu_user_str in afus_user_str:
        afu_user_bits = get_afu_num_bits(afu_user_str) - 2
        afu_user_qtde_in = get_afu_num_in(afu_user_str)
        afu_user_qtde_out = get_afu_num_out(afu_user_str)
        hw_info_str = "8'd" + str(afu_user_qtde_out) + ",8'd" + str(afu_user_qtde_in) + ",8'd" + str(
            afu_user_bits) + "," + hw_info_str

    hw_info_str = "{" + hw_info_str + "8'd" + str(tot_afu) + "}"

    hw_info.assign(EmbeddedCode(hw_info_str))

    return m

def make_dfg_only(data_width_ext, hds):
    # obter o conteúdo do arquivo hds
    hds_string = get_hds_string(hds)

    # obter nome do dfg
    dfg_name = get_dfg_name(hds_string)

    # limpar a string do hds
    hds_string = clean_hds(hds_string)

    # obter os sinais do dfg
    signals = get_dfg_signals(hds_string)

    # obter as AFUs com seus componentes
    afus_user_str = get_dfg_afus(hds_string)
    afus_user_str.sort()

    return make_afus_user(afus_user_str, signals, data_width_ext)