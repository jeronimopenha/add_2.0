from makeComponents import *
from util import *
from veriloggen import *


def makedataflow(hds, data_width_ext):
    # obter o conteúdo do arquivo hds
    hds_string = get_hds_string(hds)

    # obter nome do dfg
    dfg_name = get_dfg_name(hds_string)

    # limpar a string do hds
    hds_string = clean_hds(hds_string)

    # obter os sinais do dfg
    signals = get_dfg_signals(hds_string)

    # obter as AFUs com seus componentes
    afus_str = get_dfg_afus(hds_string)
    afus_str.sort()

    afus_mod = make_afus(afus_str, signals, data_width_ext)

    idxuut = 0
    for afu_mod in afus_mod:
        num_inputs = 0
        num_outputs = 0
        for afu_str in afus_str:
            if ("afu" + afu_str[0]) == afu_mod.name:
                num_inputs = num_inputs + get_afu_num_in(afu_str)
                num_outputs = num_outputs + get_afu_num_out(afu_str)
                break

        uut_mod = Module('uut_' + afu_mod.name)

        # sinais básicos para o funcionamento da mesma
        clk = uut_mod.Input("clk")
        rst = uut_mod.Input("rst")
        start = uut_mod.Input("start")
        done = uut_mod.Output("done")
        dconf = uut_mod.OutputReg("dconf", 32)

        # crição da lista de portas de leitura de filas para a AFU
        available_read = []
        almost_empty_read = []
        req_rd_data = []
        rd_data = []

        for i in range(0, num_inputs):
            available_read.append(uut_mod.Input("available_read%d" % i))
            almost_empty_read.append(uut_mod.Input("almost_empty_read%d" % i))
            req_rd_data.append(uut_mod.Output("req_rd_data%d" % i))
            rd_data.append(uut_mod.Input("rd_data%d" % i, data_width_ext))

        # crição da lista de portas de escrita em filas para a AFU
        available_write = []
        almost_full_write = []
        req_wr_data = []
        wr_data = []

        for i in range(0, num_outputs):
            available_write.append(uut_mod.Input("available_write%d" % i))
            almost_full_write.append(uut_mod.Input("almost_full_write%d" % i))
            req_wr_data.append(uut_mod.Output("req_wr_data%d" % i))
            wr_data.append(uut_mod.Output("wr_data%d" % i, data_width_ext))

            # criação dos fios que ligarão a primeira entradda de dados ao circuito e ao leitor de configurações

        FSM_START = uut_mod.Localparam('FSM_START', 1)
        FSM_IDLE = uut_mod.Localparam('FSM_IDLE', 1)
        FSM_READ = uut_mod.Localparam('FSM_READ', 2)
        FSM_CONF_OUT = uut_mod.Localparam('FSM_CONF_OUT', 3)
        FSM_START_AFUS = uut_mod.Localparam('FSM_START_AFUS', 4)

        # registrador de start para as AFUS
        start_afus = uut_mod.Reg('start_afus')
        conf = uut_mod.Reg('conf', data_width_ext)
        counter_total = uut_mod.Reg('counter_total', 64)
        counter = uut_mod.Reg('counter', 64)
        qtde_conf = uut_mod.Reg('qtde_conf', 64)
        fsm_main = uut_mod.Reg('fsm_main', 3)
        req_rd_data0_conf = uut_mod.Reg("req_rd_data0_conf")
        req_rd_data0_afu = uut_mod.Wire("req_rd_data0_afu")
        req_rd_data[0].assign(req_rd_data0_conf | req_rd_data0_afu)

        uut_mod.Always()(
            If(rst)(
                start_afus(Int(0, 1, 2)),
                counter_total(Int(0, counter_total.width, 2)),
                counter(Int(0, counter.width, 2)),
                qtde_conf(Int(0, qtde_conf.width, 2)),
                req_rd_data0_conf(Int(0, 1, 2)),
                dconf(Int(0, dconf.width, 2)),
                fsm_main(FSM_START),
            ).Elif(start)(
                req_rd_data0_conf(Int(0, 1, 2)),
                Case(fsm_main)(
                    When(FSM_START)(
                        If(req_rd_data0_conf)(
                            qtde_conf(rd_data[0][0:64]),
                            fsm_main(FSM_IDLE),
                        ).Elif(available_read[0])(
                            req_rd_data0_conf(Int(1, 1, 2)),
                        ),
                    ),
                    When(FSM_IDLE)(
                        If(counter_total == qtde_conf)(
                            fsm_main(FSM_START_AFUS),
                        ).Elif(available_read[0])(
                            req_rd_data0_conf(Int(1, 1, 2)),
                            fsm_main(FSM_READ),
                        )
                    ),
                    When(FSM_READ)(
                        conf(rd_data[0]),
                        counter(Int(0, counter.width, 10)),
                        fsm_main(FSM_CONF_OUT)
                    ),
                    When(FSM_CONF_OUT)(
                        dconf(conf[0: 32]),
                        conf(conf >> Int(32, 6, 10)),
                        counter_total(counter_total + Int(1, counter_total.width, 2)),
                        counter(counter + Int(1, counter.width, 2)),

                        If(counter_total == qtde_conf - 1)(
                            fsm_main(FSM_START_AFUS),
                        ).Elif(counter == (data_width_ext // 32) - 1)(
                            fsm_main(FSM_IDLE),
                        ).Else(
                            fsm_main(FSM_CONF_OUT),
                        )
                    ), When(FSM_START_AFUS)(
                        dconf(Int(0, dconf.width, 2)),
                        start_afus(Int(1, 1, 2)),
                    )
                )
            )
        )

        # instanciar a AFU
        params = []
        con = [("clk", clk), ("rst", rst), ("start", start_afus), ("done", done), ("dconf", dconf)]

        for i in range(0, num_inputs):
            con.append(('available_read%d' % i, available_read[i]))
            con.append(('almost_empty_read%d' % i, almost_empty_read[i]))
            if i == 0:
                con.append(('req_rd_data%d' % i, req_rd_data0_afu))
            else:
                con.append(('req_rd_data%d' % i, req_rd_data[i]))
            con.append(('rd_data%d' % i, rd_data[i]))
        for i in range(0, num_outputs):
            con.append(('available_write%d' % i, available_write[i]))
            con.append(('almost_full_write%d' % i, almost_full_write[i]))
            con.append(('req_wr_data%d' % i, req_wr_data[i]))
            con.append(('wr_data%d' % i, wr_data[i]))

        uut_mod.Instance(afu_mod, afu_mod.name, params, con)

        uut_mod.to_verilog("verilog/" + dfg_name)


def make_afus(afus, signals, data_width_ext):
    afus_mod = []
    # para cada AFU encontrada
    for afu in afus:
        # encontra a quantidade de entradas e saídas
        num_inputs = get_afu_num_in(afu)
        num_outputs = get_afu_num_out(afu)

        # lista para facilitar a criação de componentes similares
        # MELHORAR! MELHORAR! MELHORAR! MELHORAR! MELHORAR! MELHORAR! MELHORAR!
        simple_bin_components = ["SyncAdd", "SyncAnd", "SyncDiv", "SyncMax", "SyncMin", "SyncMod", "SyncMul",
                                 "SyncOr", "SyncShl", "SyncShr", "SyncSlt", "SyncSub"]
        simple_immediate_components = ["SyncAddI", "SyncAndI", "SyncDivI", "SyncModI", "SyncMulI", "SyncOrI",
                                       "SyncShlI", "SyncShrI", "SyncSltI", "SyncSubI", ]
        simple_unary_components = ["SyncAbs", "SyncRegister", "SyncNot"]
        # criação do módulo da afu
        afu_mod = Module("afu" + afu[0])

        # sinais básicos para o funcionamento da mesma
        clk = afu_mod.Input("clk")
        rst = afu_mod.Input("rst")
        start = afu_mod.Input("start")
        done = afu_mod.Output("done")
        dconf = afu_mod.Input("dconf", 32)

        # crição da lista de portas de leitura de filas para a AFU
        available_read = []
        almost_empty_read = []
        req_rd_data = []
        rd_data = []

        for i in range(0, num_inputs):
            available_read.append(afu_mod.Input("available_read%d" % i))
            almost_empty_read.append(afu_mod.Input("almost_empty_read%d" % i))
            req_rd_data.append(afu_mod.Output("req_rd_data%d" % i))
            rd_data.append(afu_mod.Input("rd_data%d" % i, data_width_ext))

        # crição da lista de portas de escrita em filas para a AFU
        available_write = []
        almost_full_write = []
        req_wr_data = []
        wr_data = []

        for i in range(0, num_outputs):
            available_write.append(afu_mod.Input("available_write%d" % i))
            almost_full_write.append(afu_mod.Input("almost_full_write%d" % i))
            req_wr_data.append(afu_mod.Output("req_wr_data%d" % i))
            wr_data.append(afu_mod.Output("wr_data%d" % i, data_width_ext))

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
        streams_ready = afu_mod.Wire('streams_ready', num_inputs + num_outputs)
        en = afu_mod.Wire("en")
        en.assign(EmbeddedCode("&streams_ready"))
        done_wire = afu_mod.Wire('done_wire', num_outputs)
        done.assign(EmbeddedCode("&done_wire"))

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
                if (component_type in simple_bin_components):
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
                elif (component_type in simple_unary_components):
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

                # componentes unários simples, com imediatos, de mesma quantidade de parâmetros para serem gerados
                elif (component_type in simple_immediate_components):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2)
                        component_mod_dict[component_type] = component_mod

                    con = [('clk', clk), ('rst', rst), ("en", en), ("dconf", dconf)]
                    params = [('ID', int(component[8])), ('IMMEDIATE', int(component[9]))]

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
                           ('rd_available', available_read[idx_in]),
                           ('rd_almost_empty', almost_empty_read[idx_in]), ('rd_data', rd_data[idx_in]),
                           ('req_rd_data', req_rd_data[idx_in]), ('component_ready', streams_ready[idx_stream])]
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

                # componentes de saída de dados "out"
                elif (component_type == "SyncOut"):
                    if component_type in component_mod_dict:
                        component_mod = component_mod_dict[component_type]
                    else:
                        component_mod = functions(component_type)(component_type, int(component[6]) - 2, data_width_ext)
                        component_mod_dict[component_type] = component_mod
                    con = [('clk', clk), ('rst', rst), ('start', start), ('wr_available', available_write[idx_out]),
                           ('wr_almost_full', almost_full_write[idx_out]), ('wr_data', wr_data[idx_out]),
                           ('wr_req_data', req_wr_data[idx_out]), ('component_ready', streams_ready[idx_stream]),
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
        afus_mod.append(afu_mod)
    return afus_mod
