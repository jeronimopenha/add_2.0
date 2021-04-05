from veriloggen import *

from make_operators import *
from util import *


def make_afus_user(afus, signals, data_width_ext):
    afus_mod = []
    # para cada AFU encontrada
    for afu in afus:
        # encontra a quantidade de entradas e saídas
        qtde_inputs = get_afu_num_in(afu)
        qtde_outputs = get_afu_num_out(afu)

        # lista para facilitar a criação de componentes similares
        # MELHORAR! MELHORAR! MELHORAR! MELHORAR! MELHORAR! MELHORAR! MELHORAR!
        simple_sync_bin_components = ["SyncAdd", "SyncAnd", "SyncDiv", "SyncMax", "SyncMin", "SyncMod", "SyncMul",
                                      "SyncOr", "SyncShl", "SyncShr", "SyncSlt", "SyncSub", "SyncXor"]
        simple_sync_immediate_components = ["SyncAddI", "SyncAndI", "SyncDivI", "SyncModI", "SyncMulI", "SyncOrI",
                                            "SyncShlI", "SyncShrI", "SyncSltI", "SyncSubI", ]
        sync_knn_components = ["SyncKnnCtrl", "SyncKnnQueue"]

        simple_sync_unary_components = ["SyncAbs", "SyncRegister", "SyncNot"]

        simple_async_unary_components = {"AsyncGrn"}

        # criação do módulo da afu
        afu_mod = Module("afu_user_" + afu[0])

        DATA_WIDTH = afu_mod.Parameter("DATA_WIDTH", 32)
        NUM_INPUT_QUEUES = afu_mod.Parameter("NUM_INPUT_QUEUES", 1)
        NUM_OUTPUT_QUEUES = afu_mod.Parameter("NUM_OUTPUT_QUEUES", 1)

        # sinais básicos para o funcionamento da mesma
        clk = afu_mod.Input("clk")
        rst = afu_mod.Input("rst")
        start = afu_mod.Input("start")
        afu_user_done_rd_data = afu_mod.Input("afu_user_done_rd_data", qtde_inputs)
        afu_user_done_wr_data = afu_mod.Input("afu_user_done_wr_data", qtde_outputs)

        afu_user_available_read = afu_mod.Input("afu_user_available_read", qtde_inputs)
        afu_user_read_data = afu_mod.Input("afu_user_read_data", qtde_inputs * data_width_ext)
        afu_user_request_read = afu_mod.Output("afu_user_request_read", qtde_inputs)
        afu_user_read_data_valid = afu_mod.Input("afu_user_read_data_valid", qtde_inputs)
        afu_user_available_write = afu_mod.Input("afu_user_available_write", qtde_outputs)
        afu_user_write_data = afu_mod.Output("afu_user_write_data", qtde_outputs * data_width_ext)
        afu_user_request_write = afu_mod.Output("afu_user_request_write", qtde_outputs)
        afu_user_done = afu_mod.OutputReg("afu_user_done")

        # identificação dos fios que esta AFU possui e criação dos mesmos
        wires = {}
        for signal in signals:
            flag = False
            for com_port in signal[2]:
                for component in afu[1]:
                    if com_port not in component:
                        continue
                    else:
                        wires[signal[0]] = afu_mod.Wire(signal[0], signal[1])
                        flag = True
                        break
                if flag:
                    break

        # controle de execução e done
        streams_ready = afu_mod.Wire('streams_ready', qtde_inputs + qtde_outputs)
        en = afu_mod.Wire("en")
        en.assign(EmbeddedCode("&streams_ready"))
        done_wire = afu_mod.Wire('done_wire', qtde_outputs)

        afu_mod.Always(Posedge(clk))(
            afu_user_done(EmbeddedCode("&done_wire")),
        )

        # Criação e instancialização dos módulos que compoem a AFU
        afu[1].sort()
        component_mod_dict = {}
        idx_in = 0
        idx_out = 0
        idx_stream = 0
        for component_str in afu[1]:
            component = component_str.split(" ")
            if "CLKCONNECTOR" not in component_str and "CLOCK" not in component_str:
                component_type = (component[0].split("."))[3]

                # componentes binários simples de mesma quantidade de parâmetros para serem gerados
                if (component_type in simple_sync_bin_components):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2)
                        component_mod_dict[component_type] = component_mod
                    con = [('clk', clk), ('rst', rst), ("en", en)]
                    params = []

                    # encontrar os fios que ligam estes componentes em outros
                    for signal in signals:
                        dict = signal[2]
                        if component[1] in dict:
                            if (dict[component[1]] == "din0"):
                                con.append(("din0", wires[signal[0]]))
                            elif (dict[component[1]] == "din1"):
                                con.append(("din1", wires[signal[0]]))
                            elif (dict[component[1]] == "dout0"):
                                con.append(("dout0", wires[signal[0]]))
                    afu_mod.Instance(component_mod, component[1], params, con)

                # componentes unários simples de mesma quantidade de parâmetros para serem gerados
                elif (component_type in simple_sync_unary_components):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2)
                        component_mod_dict[component_type] = component_mod

                    con = [('clk', clk), ('rst', rst), ("en", en)]
                    params = []

                    # encontrar os fios que ligam estes componentes em outros
                    for signal in signals:
                        dict = signal[2]
                        if component[1] in dict:
                            if (dict[component[1]] == "din0"):
                                con.append(("din0", wires[signal[0]]))
                            elif (dict[component[1]] == "dout0"):
                                con.append(("dout0", wires[signal[0]]))
                    afu_mod.Instance(component_mod, component[1], params, con)

                # componentes para knn
                elif (component_type in sync_knn_components):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2)
                        component_mod_dict[component_type] = component_mod

                    con = [('clk', clk), ('rst', rst), ("en", en)]
                    params = []

                    # encontrar os fios que ligam estes componentes em outros
                    for signal in signals:
                        dict = signal[2]
                        if component[1] in dict:
                            if (dict[component[1]] == "din0"):
                                con.append(("din0", wires[signal[0]]))
                            elif (dict[component[1]] == "din1"):
                                con.append(("din1", wires[signal[0]]))
                            elif (dict[component[1]] == "dout0"):
                                con.append(("dout0", wires[signal[0]]))
                            elif (dict[component[1]] == "dout1"):
                                con.append(("dout1", wires[signal[0]]))
                    afu_mod.Instance(component_mod, component[1], params, con)

                # componentes unários simples, com imediatos, de mesma quantidade de parâmetros para serem gerados
                elif (component_type in simple_sync_immediate_components):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2, False)
                        component_mod_dict[component_type] = component_mod

                    con = [('clk', clk), ('rst', rst), ("en", en)]
                    params = [('ID', int(component[8])), ('IMMEDIATE', (int(component[9]) & 0xffffffff))]

                    # encontrar os fios que ligam estes componentes em outros
                    for signal in signals:
                        dict = signal[2]
                        if component[1] in dict:
                            if (dict[component[1]] == "din0"):
                                con.append(("din0", wires[signal[0]]))
                            elif (dict[component[1]] == "dout0"):
                                con.append(("dout0", wires[signal[0]]))
                    afu_mod.Instance(component_mod, component[1], params, con)

                # componentes de entrada de dados "in"
                elif (component_type == "SyncIn"):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2, data_width_ext)
                        component_mod_dict[component_type] = component_mod
                    con = [('clk', clk), ('rst', rst), ("en", en), ('start', start),
                           ("rd_done", afu_user_done_rd_data[idx_in]),
                           ('rd_available', afu_user_available_read[idx_in]),
                           ("rd_valid", afu_user_read_data_valid[idx_in]),
                           ('rd_data',
                            afu_user_read_data[idx_in * data_width_ext:(idx_in * data_width_ext) + data_width_ext]),
                           ('rd_en', afu_user_request_read[idx_in]), ('component_ready', streams_ready[idx_stream])]
                    params = []

                    idx_in = idx_in + 1
                    idx_stream = idx_stream + 1

                    # encontrar os fios que ligam este componente em outros
                    for signal in signals:
                        dict = signal[2]
                        if component[1] in dict:
                            if (dict[component[1]] == "dout0"):
                                con.append(("dout0", wires[signal[0]]))
                    afu_mod.Instance(component_mod, component[1], params, con)

                # componentes de entrada de dados "in"
                elif (component_type == "AsyncIn"):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2, data_width_ext)
                        component_mod_dict[component_type] = component_mod
                    con = [('clk', clk), ('rst', rst), ('start', start),
                           ("rd_done", afu_user_done_rd_data[idx_in]),
                           ('rd_available', afu_user_available_read[idx_in]),
                           ("rd_valid", afu_user_read_data_valid[idx_in]),
                           ('rd_data',
                            afu_user_read_data[idx_in * data_width_ext:(idx_in * data_width_ext) + data_width_ext]),
                           ('rd_en', afu_user_request_read[idx_in])]
                    params = []

                    idx_in = idx_in + 1

                    # encontrar os fios que ligam este componente em outros
                    for signal in signals:
                        dict = signal[2]
                        if component[1] in dict:
                            if (dict[component[1]] == "dout0"):
                                con.append(("dout0", wires[signal[0]]))
                            elif (dict[component[1]] == "reqr0"):
                                con.append(("reqr0", wires[signal[0]]))
                            elif (dict[component[1]] == "ackr0"):
                                con.append(("ackr0", wires[signal[0]]))

                    afu_mod.Instance(component_mod, component[1], params, con)

                # componentes GRN
                elif (component_type in simple_async_unary_components):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2)
                        component_mod_dict[component_type] = component_mod
                    con = [('clk', clk), ('rst', rst)]
                    params = []

                    # encontrar os fios que ligam este componente em outros
                    for signal in signals:
                        dict = signal[2]
                        if component[1] in dict:
                            if (dict[component[1]] == "dout0"):
                                con.append(("dout0", wires[signal[0]]))
                            if (dict[component[1]] == "din0"):
                                con.append(("din0", wires[signal[0]]))
                            elif (dict[component[1]] == "reqr0"):
                                con.append(("reqr0", wires[signal[0]]))
                            elif (dict[component[1]] == "ackr0"):
                                con.append(("ackr0", wires[signal[0]]))
                            elif (dict[component[1]] == "reql0"):
                                con.append(("reql0", wires[signal[0]]))
                            elif (dict[component[1]] == "ackl0"):
                                con.append(("ackl0", wires[signal[0]]))

                    afu_mod.Instance(component_mod, component[1], params, con)

                # componentes de saída de dados "out"
                elif (component_type == "SyncOut"):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2, data_width_ext)
                        component_mod_dict[component_type] = component_mod
                    con = [('clk', clk), ('rst', rst), ('start', start), ("en", en),
                           ('wr_available', afu_user_available_write[idx_out]),
                           ('wr_data',
                            afu_user_write_data[idx_out * data_width_ext:(idx_out * data_width_ext) + data_width_ext]),
                           ('wr_en', afu_user_request_write[idx_out]), ('component_ready', streams_ready[idx_stream]),
                           ('done', done_wire[idx_out])]
                    params = []

                    idx_out = idx_out + 1
                    idx_stream = idx_stream + 1

                    # encontrar os fios que ligam este componente em outros
                    for signal in signals:
                        dict = signal[2]
                        if component[1] in dict:
                            if (dict[component[1]] == "din0"):
                                con.append(("din0", wires[signal[0]]))
                    afu_mod.Instance(component_mod, component[1], params, con)

                # componentes de saída de dados "out"
                elif (component_type == "AsyncOut"):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2, data_width_ext)
                        component_mod_dict[component_type] = component_mod
                    con = [('clk', clk), ('rst', rst), ('start', start),
                           ('wr_available', afu_user_available_write[idx_out]),
                           ('wr_data',
                            afu_user_write_data[idx_out * data_width_ext:(idx_out * data_width_ext) + data_width_ext]),
                           ('wr_en', afu_user_request_write[idx_out]),
                           ('done', done_wire[idx_out])]
                    params = []

                    idx_out = idx_out + 1

                    # encontrar os fios que ligam este componente em outros
                    for signal in signals:
                        dict = signal[2]
                        if component[1] in dict:
                            if (dict[component[1]] == "din0"):
                                con.append(("din0", wires[signal[0]]))
                            elif (dict[component[1]] == "reql0"):
                                con.append(("reql0", wires[signal[0]]))
                            elif (dict[component[1]] == "ackl0"):
                                con.append(("ackl0", wires[signal[0]]))
                    afu_mod.Instance(component_mod, component[1], params, con)
        afus_mod.append(afu_mod)
    return afus_mod
