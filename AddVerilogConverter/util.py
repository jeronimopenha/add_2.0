def get_hds_string(file_name):
    hds_file = open(file_name, 'r')
    return hds_file.read()


def get_dfg_name(hds_string):
    linhas = hds_string.split('\n')
    return linhas[2].split(' ')[1]


def clean_hds(hds_string):
    new_hds_string = ""
    lines = hds_string.split('\n')
    for line in lines:
        if "#" in line or "[" in line or line == "":
            continue
        elif new_hds_string == "":
            new_hds_string = line
        else:
            new_hds_string = new_hds_string + "\n" + line
    return new_hds_string


def get_dfg_signals(hds_string):
    signals = []
    lines = hds_string.split('\n')
    lines.sort()
    for line in lines:
        if "SignalStdLogicVector" in line:
            signal_comp = {}
            signal = []
            signal_str = line.split(" ")
            signal.append(signal_str[1])
            signal.append(int(signal_str[2]))
            for i in range(0, int(signal_str[3])):
                signal_comp[signal_str[4 + (2 * i)]] = signal_str[4 + 1 + (2 * i)]
            signal.append(signal_comp)
            signals.append(signal)
    return signals


def discover_afus(hdsstring):
    afus_number = []
    lines = hdsstring.split('\n')
    lines.sort()
    for line in lines:
        if "add.dataflow" in line and "CLKCONNECTOR" not in line and "CLOCK" not in line:
            component = line.split(" ")
            if component[len(component) - 1] not in afus_number:
                afus_number.append(component[len(component) - 1])
    return afus_number


def get_dfg_afus(hds_string):
    afus_number = discover_afus(hds_string)
    afus = []
    lines = hds_string.split('\n')
    lines.sort()
    for afu_num in afus_number:
        comps = []
        afu = []
        afu.append(afu_num)
        for linha in lines:
            if "add.dataflow" in linha and "CLKCONNECTOR" not in linha and "CLOCK" not in linha:
                component = linha.split(" ")
                if component[len(component) - 1] == afu_num:
                    comps.append(linha)
        afu.append(comps)
        afus.append(afu)
    return afus


def get_afu_num_in(afu_str):
    num_in = 0
    for component in afu_str[1]:
        if "SyncIn" in component or "AsyncIn" in component:
            num_in = num_in + 1

    return num_in


def get_afu_num_out(afu_str):
    num_out = 0
    for component in afu_str[1]:
        if "SyncOut" in component or "AsyncOut" in component:
            num_out = num_out + 1

    return num_out
