from math import *
from pkg_resources import MODULE
from veriloggen import *

# simple binary operators. BEGIN
from gnr.make_regulator_network import make_regulator_network
from gnr.readFile import readFile


def make_sync_add(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width] + din1[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_and(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width] & din1[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_div(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width] / din1[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_max(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10),
                              Mux((din0[0:data_width] > din1[0:data_width]), din0[0:data_width],
                                  din1[0:data_width])))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_min(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10),
                              Mux((din0[0:data_width] < din1[0:data_width]), din0[0:data_width],
                                  din1[0:data_width])))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_mod(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width] % din1[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_mul(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width] * din1[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_or(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width] | din1[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_shl(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width] << din1[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_shr(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width] >> din1[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_slt(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10),
                              Mux((din0[0:data_width] < din1[0:data_width]), Int(1, data_width, 10),
                                  Int(0, data_width, 10))))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_sub(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width] - din1[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_xor(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0[realdatawitdh - 2:realdatawitdh], din1[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width] ^ din1[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


# simple binary operators. END

# simple unary operators with immediate values. BEGIN
def make_sync_addi(name, data_width, conf):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    dconf = m.Input('dconf', dconf_width)

    immediate = m.Reg('immediate', dconf_width + id_width)

    if (conf):
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] + immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] + IMMEDIATE[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    return m


def make_sync_andi(name, data_width, conf):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    dconf = m.Input('dconf', dconf_width)

    immediate = m.Reg('immediate', dconf_width + id_width)

    if (conf):
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] & immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] & IMMEDIATE[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    return m


def make_sync_divi(name, data_width, conf):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    dconf = m.Input('dconf', dconf_width)

    immediate = m.Reg('immediate', dconf_width + id_width)

    if (conf):
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] / immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] / IMMEDIATE[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    return m


def make_sync_modi(name, data_width, conf):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    dconf = m.Input('dconf', dconf_width)

    immediate = m.Reg('immediate', dconf_width + id_width)

    if (conf):
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] % immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] % IMMEDIATE[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    return m


def make_sync_muli(name, data_width, conf):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    dconf = m.Input('dconf', dconf_width)

    immediate = m.Reg('immediate', dconf_width + id_width)

    if (conf):
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] * immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] * IMMEDIATE[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    return m


def make_sync_ori(name, data_width, conf):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    dconf = m.Input('dconf', dconf_width)

    immediate = m.Reg('immediate', dconf_width + id_width)

    if (conf):
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] | immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] | IMMEDIATE[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    return m


def make_sync_shli(name, data_width, conf):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    dconf = m.Input('dconf', dconf_width)

    immediate = m.Reg('immediate', dconf_width + id_width)

    if (conf):
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] << immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] << IMMEDIATE[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    return m


def make_sync_shri(name, data_width, conf):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    dconf = m.Input('dconf', dconf_width)

    immediate = m.Reg('immediate', dconf_width + id_width)

    if (conf):
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] >> immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] >> IMMEDIATE[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    return m


def make_sync_slti(name, data_width, conf):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    dconf = m.Input('dconf', dconf_width)

    immediate = m.Reg('immediate', dconf_width + id_width)

    if (conf):
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10),
                                      Mux(din0[0:data_width] < immediate[0:data_width], Int(1, data_width, 10),
                                          Int(0, data_width, 10))))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10),
                                      Mux(din0[0:data_width] < IMMEDIATE[0:data_width], Int(1, data_width, 10),
                                          Int(0, data_width, 10))))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    return m


def make_sync_subi(name, data_width, conf):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    dconf = m.Input('dconf', dconf_width)

    immediate = m.Reg('immediate', dconf_width + id_width)

    if (conf):
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] - immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0[0:data_width] - IMMEDIATE[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    return m


# simple unary operators with immediate values. END

# simple unary operators values. BEGIN
def make_sync_abs(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10)),
        ).Elif(en)(
            EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
            Case(din0[realdatawitdh - 2:realdatawitdh])(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Int(2, 2, 2))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Int(1, 2, 2))(  # Valid = 1
                    dout0(
                        Cat(Int(1, 2, 10),
                            Mux(din0[data_width - 1], ((~din0[0:data_width]) + 1), din0[0:data_width])))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_register(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10)),
        ).Elif(en)(
            EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
            Case(din0[realdatawitdh - 2:realdatawitdh])(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Int(2, 2, 2))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Int(1, 2, 2))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_not(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, realdatawitdh, 10)),
        ).Elif(en)(
            EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
            Case(din0[realdatawitdh - 2:realdatawitdh])(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Int(2, 2, 2))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Int(1, 2, 2))(  # Valid = 1
                    dout0(
                        Cat(Int(1, 2, 10), ~din0[0:data_width]))
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_knnctrl(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)
    dout1 = m.OutputReg('dout1', 2)

    knn_distance = m.Reg("knn_distance", data_width)
    flag = m.Reg("flag")

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, dout0.width, 10)),
            dout1(Int(0, dout1.width, 10)),
            knn_distance(Int(0, knn_distance.width, 10)),
            flag(Int(0, 1, 2)),
        ).Elif(en)(
            EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
            Case(din0[realdatawitdh - 2:realdatawitdh])(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, dout0.width, 10)),
                    dout1(Int(0, dout1.width, 10)),
                ), When(Int(2, 2, 2))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10))),
                    dout1(Int(2, dout1.width, 10)),
                ), When(Int(1, 2, 2))(  # Valid = 1
                    If(Not(flag))(
                        knn_distance(din0[0:data_width]),
                        flag(Int(1, 1, 2)),
                        dout0(Cat(Int(1, 2, 10), Int(int(pow(2, data_width - 1) - 1), data_width, 10))),
                        dout1(Int(1, dout1.width, 10)),
                    ).Elif(knn_distance > din0[0:data_width])(
                        knn_distance(din0[0:data_width]),
                        dout0(Cat(Int(1, 2, 10), knn_distance)),
                        dout1(Int(1, dout1.width, 10)),
                    ).Else(
                        dout0(Cat(Int(1, 2, 10), din0[0:data_width])),
                        dout1(Int(0, dout1.width, 10)),
                    )
                ), When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


def make_sync_knnqueue(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', 2)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    knn_value = m.Reg("knn_value", data_width)
    last_scan = m.Reg("last_scan")

    m.Always(Posedge(clk))(
        If(rst)(
            dout0(Int(0, dout0.width, 10)),
            knn_value(Int(0, knn_value.width, 10)),
            last_scan(Int(0, 1, 2)),
        ).Elif(en)(
            EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
            Case(din1[realdatawitdh - 2:realdatawitdh])(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, dout0.width, 10)),
                ), When(Int(2, 2, 2))(  # Done = 2
                    If(Not(last_scan))(
                        dout0(Cat(Int(1, 2, 10), knn_value)),
                        last_scan(Int(1, 1, 2)),
                    ).Else(
                        dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10))),
                    )
                ), When(Int(1, 2, 2))(  # Valid = 1
                    If(din0 == Int(0, din0.width, 10))(
                        dout0(Cat(Int(3, 2, 10), din1[0:data_width])),
                    ).Elif(din0 == Int(1, din0.width, 10))(
                        knn_value(din1[0:data_width]),
                        dout0(Cat(Int(3, 2, 10), knn_value)),
                    ).Else(
                        knn_value(din1[0:data_width]),
                        dout0(Cat(Int(1, 2, 10), knn_value)),
                    )
                ), When(Int(3, 2, 2))(  # OPT = 3
                    If(din0 == Int(0, din0.width, 10))(
                        dout0(Cat(Int(3, 2, 10), din1[0:data_width])),
                    ).Elif(din0 == Int(1, din0.width, 10))(
                        knn_value(din1[0:data_width]),
                        dout0(Cat(Int(3, 2, 10), knn_value)),
                    )
                ),
                When()(  # I will consider this like STOP
                    dout0(Int(0, realdatawitdh, 10))
                )
            )
        )
    )
    return m


# simple unary operators values. End

# GNR Assíncrono

def make_async_grn(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    # basic signals BEGIN
    clk = m.Input('clk')
    rst = m.Input('rst')
    # basic signals END

    # inputdata BEGIN
    reql0 = m.Input("reql0")
    ackl0 = m.OutputReg("ackl0")
    din0 = m.Input('din0', data_width + 2)
    # inputdata END

    # outputdata BEGIN
    reqr0 = m.OutputReg("reqr0")
    ackr0 = m.Input("ackr0")
    dout0 = m.OutputReg('dout0', data_width + 2)
    # outputdata END

    # path = 'gnr/benchmarks/Benchmark_5.txt'
    path = 'gnr/benchmarks/Benchmark_70.txt'
    func = readFile(path)

    num_units = 1
    id_width = int(ceil(log(num_units, 2))) + 1
    width_data_in = 2 * len(func) + id_width
    qtde_words_state = ceil(width_data_in / 32.0)

    gnr_start = m.Reg("gnr_start")
    data_in_valid = m.Reg("data_in_valid")
    data_in = m.Reg("data_in", (qtde_words_state * data_width) + 1)
    end_data_in = m.Reg("end_data_in")
    read_data_en = m.Reg("read_data_en")
    has_data_out = m.Wire("has_data_out")
    has_lst3_data_out = m.Wire("has_lst3_data_out")
    data_out = m.Wire("data_out", 29 * 2)
    data_out_reg = m.Reg("data_out_reg", data_width * 2)
    data_to_send = m.Wire("data_to_send", 32, 2)
    task_done = m.Wire("task_done")

    data_to_send[0].assign(Cat(Int(0, 3, 10), data_out_reg[0:29]))
    data_to_send[1].assign(Cat(Int(0, 3, 10), data_out_reg[29:59]))

    read_data = m.Reg("read_data")
    sent_data = m.Reg("sent_data")

    fsm_read = m.Reg('fsm_read', 3)
    FSM_READ_IDLE = m.Localparam('FSM_READ_IDLE', Int(0, fsm_read.width, 10))
    FSM_READ_WAIT = m.Localparam('FSM_READ_WAIT', Int(1, fsm_read.width, 10))

    fsm_process = m.Reg('fsm_process', 4)
    FSM_PROCESS_IDLE = m.Localparam('FSM_PROCESS_IDLE', Int(0, fsm_process.width, 10))
    FSM_PROCESS_READ = m.Localparam('FSM_PROCESS_READ', Int(1, fsm_process.width, 10))
    FSM_PROCESS_WAIT_PROCESS = m.Localparam('FSM_PROCESS_WAIT_PROCESS', Int(2, fsm_process.width, 10))
    FSM_PROCESS_READ_DATA = m.Localparam('FSM_PROCESS_READ_DATA', Int(3, fsm_process.width, 10))
    FSM_PROCESS_SEND_DATA = m.Localparam('FSM_PROCESS_SEND_DATA', Int(4, fsm_process.width, 10))
    FSM_PROCESS_WAIT_ACKR = m.Localparam('FSM_PROCESS_WAIT_ACKR', Int(5, fsm_process.width, 10))
    FSM_PROCESS_DONE = m.Localparam('FSM_PROCESS_DONE', Int(6, fsm_process.width, 10))

    data = m.Reg('data', data_width + 2)
    counter_words = m.Reg('counter_words', 20)
    counter_send_data = m.Reg('counter_send_data', 2)

    # read machine
    m.Always(Posedge(clk))(
        If(rst)(
            read_data(Int(0, 1, 2)),
            ackl0(Int(0, 1, 2)),
            fsm_read(FSM_READ_IDLE),
        ).Else(
            Case(fsm_read)(
                When(FSM_READ_IDLE)(
                    If(AndList(reql0, (read_data == sent_data)))(
                        data(din0),
                        ackl0(Int(1, 1, 2)),
                        read_data(~read_data),
                        fsm_read(FSM_READ_WAIT),
                    )
                ),
                When(FSM_READ_WAIT)(
                    If(Not(reql0))(
                        ackl0(Int(0, 1, 2)),
                        fsm_read(FSM_READ_IDLE),
                    ),
                )
            )
        )
    )

    flag_wait = m.Reg("flag_wait")

    # processing machine
    m.Always(Posedge(clk))(
        If(rst)(
            sent_data(Int(0, 1, 2)),
            counter_words(Int(0, counter_words.width, 10)),
            # counter_send_data(Int(0, counter_send_data.width, 10)),
            gnr_start(Int(0, 1, 2)),
            data_in_valid(Int(0, 1, 2)),
            end_data_in(Int(0, 1, 2)),
            read_data_en(Int(0, 1, 2)),
            data_in(Int(0, data_in.width, 10)),
            fsm_process(FSM_PROCESS_IDLE),
        ).Else(
            gnr_start(Int(1, 1, 2)),
            data_in_valid(Int(0, 1, 2)),
            read_data_en(Int(0, 1, 2)),
            Case(fsm_process)(
                When(FSM_PROCESS_IDLE)(
                    If(AndList(sent_data != read_data))(
                        fsm_process(FSM_PROCESS_READ),
                    )
                ),
                When(FSM_PROCESS_READ)(
                    Case(data[realdatawitdh - 2:realdatawitdh])(
                        When(Int(1, 2, 2))(
                            If(counter_words < Int(qtde_words_state, counter_words.width, 10))(
                                data_in(Cat(data[0:data_width], data_in[data_width:data_in.width])),

                                # data_in((data[0:data_width] << (
                                #    counter_words * Int(data_width, counter_words.width, 10))) | data_in[
                                #                                                                 1:data_in.width]),
                                sent_data(~sent_data),
                                counter_words(counter_words + Int(1, counter_words.width, 10)),
                                fsm_process(FSM_PROCESS_IDLE),
                            ).Else(
                                data_in(data_in >> Int(1,1,2)),
                                data_in_valid(Int(1, 1, 2)),
                                fsm_process(FSM_PROCESS_WAIT_PROCESS),
                            )
                        ),
                    ),
                ),
                When(FSM_PROCESS_WAIT_PROCESS)(
                    end_data_in(Int(1, 1, 2)),
                    If(has_data_out)(
                        read_data_en(Int(1, 1, 2)),
                        flag_wait(Int(0, 1, 2)),
                        fsm_process(FSM_PROCESS_READ_DATA),
                    ).Elif(task_done)(
                        If(Not(ackr0))(
                            dout0(Cat(Int(2, 2, 2), Int(0, data_width, 10))),
                            reqr0(Int(1, 1, 2)),
                            fsm_process(FSM_PROCESS_DONE),
                        )
                    ),
                ),
                When(FSM_PROCESS_READ_DATA)(
                    If(Not(flag_wait))(
                        flag_wait(Int(1, 1, 2)),
                    ).Else(
                        data_out_reg(data_out),
                        counter_send_data(Int(0, counter_send_data.width, 10)),
                        fsm_process(FSM_PROCESS_SEND_DATA)
                    ),
                ),
                When(FSM_PROCESS_SEND_DATA)(
                    If(Not(ackr0))(
                        dout0(Cat(Int(1, 2, 2), data_to_send[counter_send_data[0]])),
                        counter_send_data(counter_send_data + Int(1, counter_send_data.width, 10)),
                        reqr0(Int(1, 1, 2)),
                        fsm_process(FSM_PROCESS_WAIT_ACKR),
                    ),
                ),
                When(FSM_PROCESS_WAIT_ACKR)(
                    If(ackr0)(
                        reqr0(Int(0, 1, 2)),
                        If(counter_send_data[1])(
                            fsm_process(FSM_PROCESS_WAIT_PROCESS),
                        ).Else(
                            fsm_process(FSM_PROCESS_SEND_DATA),
                        )
                    )
                ),
                When(FSM_PROCESS_DONE)(
                    If(ackr0)(
                        reqr0(Int(0, 1, 2)),
                    )
                )
            )
        )
    )

    gnr_network = make_regulator_network(1, func, 2 ** 3, 2 ** 12)
    con = [("clk", clk), ("rst", rst), ("start", gnr_start), ("data_in_valid", data_in_valid), ("data_in", data_in),
           ("end_data_in", end_data_in), ("read_data_en", read_data_en), ("has_data_out", has_data_out),
           ("has_lst3_data_out", has_lst3_data_out), ("data_out", data_out), ("task_done", task_done)]
    params = [("ID", 0)]
    m.Instance(gnr_network, "gnr_network", params, con)

    return m


# Component that receives the input buffer BEGIN
def make_sync_in(name, data_width, data_width_ext):
    m = Module(name)

    # basic signals BEGIN
    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    start = m.Input('start')
    rd_done = m.Input("rd_done")
    # basic signals END

    # fifo_in control BEGIN
    rd_available = m.Input('rd_available')
    rd_data = m.Input('rd_data', data_width_ext)
    rd_valid = m.Input('rd_valid')
    rd_en = m.OutputReg('rd_en')
    # fifo_in control END

    # control of data entry components BEGIN
    component_ready = m.OutputReg('component_ready')
    # control of data entry components END

    # outputdata BEGIN
    dout0 = m.OutputReg('dout0', data_width + 2)
    # outputdata END


    FSM_IDLE = m.Localparam('FSM_IDLE', 1)
    FSM_READ = m.Localparam('FSM_READ', 2)
    FSM_DATA_OUT = m.Localparam('FSM_DATA_OUT', 3)
    FSM_DONE = m.Localparam('FSM_DONE', 4)

    data = m.Reg('data', data_width_ext)
    counter = m.Reg('counter', 6)

    fsm_main = m.Reg('fsm_main', 3)

    m.Always(Posedge(clk))(
        If(rst)(
            component_ready(Int(0, 1, 2)),
            rd_en(Int(0, 1, 2)),
            counter(Int(0, counter.width, 10)),
            fsm_main(FSM_IDLE),
        ).Elif(start)(
            rd_en(Int(0, 1, 2)),
            component_ready(Int(0, 1, 2)),
            Case(fsm_main)(
                When(FSM_IDLE)(
                    If(rd_available)(
                        rd_en(Int(1, 1, 2)),
                        fsm_main(FSM_READ),
                    ).Elif(AndList(rd_done, Not(rd_available)))(
                        fsm_main(FSM_DONE),
                    )
                ),
                When(FSM_READ)(
                    If(rd_valid)(
                        data(rd_data),
                        counter(Int(0, counter.width, 10)),
                        # component_ready(Int(1, 1, 2)),
                        dout0(Int(0, dout0.width, 2)),
                        fsm_main(FSM_DATA_OUT)
                    )
                ),
                When(FSM_DATA_OUT)(
                    component_ready(Int(1, 1, 2)),
                    If(en)(
                        dout0(Cat(Int(1, 2, 10), data[0: data_width])),
                        data(data >> Int(data_width, 6, 10)),
                        counter(counter + Int(1, counter.width, 2)),
                        If(counter == (data_width_ext // data_width) - 1)(
                            fsm_main(FSM_IDLE),
                        ).Else(
                            fsm_main(FSM_DATA_OUT),
                        )
                    ),
                ),
                When(FSM_DONE)(
                    dout0(Cat(Int(2, 2, 2), Int(0, data_width, 10))),
                    component_ready(Int(1, 1, 2)),
                    fsm_main(FSM_DONE),
                ),
            )
        )
    )

    return m


# Component the receives the input buffer END

# Component that records back data to the output buffer BEGIN
def make_sync_out(name, data_width, data_width_ext):
    m = Module(name)
    realdatawitdh = data_width + 2

    # basic signals BEGIN
    clk = m.Input('clk')
    rst = m.Input('rst')
    start = m.Input('start')
    en = m.Input("en")
    # basic signals END

    # fifo_out control BEGIN [1-1:0] afu_user_wr_en,
    wr_available = m.Input('wr_available')
    wr_en = m.OutputReg('wr_en')
    wr_data = m.OutputReg('wr_data', data_width_ext)
    # fifo_out control END

    # control of data entry components BEGIN
    component_ready = m.OutputReg('component_ready')
    # control of data entry components END

    # inputdata BEGIN
    din0 = m.Input('din0', realdatawitdh)
    # inputdata END

    # DONE signal
    done = m.OutputReg('done')

    DATA_WIDTH = m.Localparam("DATA_WIDTH", Int(data_width, data_width, 10))

    data = m.Reg('data', data_width_ext)
    counter = m.Reg('counter', 6)
    wr_flag = m.Reg("wr_flag")

    if data_width == data_width_ext:

        m.Always(Posedge(clk))(
            If(rst)(
                wr_en(Int(0, 1, 2)),
                component_ready(Int(0, 1, 2)),
                counter(Int(0, counter.width, 10)),
                done(Int(0, 1, 2)),
                wr_flag(Int(0, 1, 2)),
                data(din0[0:data_width]),
            ).Elif(AndList(start, Not(done)))(
                EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                component_ready(Int(1, 1, 2)),
                wr_en(Int(0, 1, 2)),
                If(Not(wr_available))(
                    component_ready(Int(0, 1, 2)),
                ).Elif(en)(
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(2, 2, 2))(  # Done = 2
                            component_ready(Int(1, 1, 2)),
                            If(wr_flag)(
                                wr_flag(Int(0, 1, 2)),
                                wr_data(data),
                                wr_en(Int(1, 1, 2)),
                            ).Else(
                                done(Int(1, 1, 2)),
                            )
                        ),
                        When(Int(1, 2, 2))(  # Valid = 1
                            If(counter == (data_width_ext // data_width) - 1)(
                                counter(Int(0, counter.width, 10)),
                                wr_flag(Int(0, 1, 2)),
                                wr_data(din0[0:data_width]),
                                data(din0[0:data_width]),
                                wr_en(Int(1, 1, 2)),
                            ).Else(
                                wr_flag(Int(1, 1, 2)),
                                counter(counter + Int(1, counter.width, 10)),
                                data(din0[0:data_width]),
                            ),
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst)(
                wr_en(Int(0, 1, 2)),
                component_ready(Int(0, 1, 2)),
                counter(Int(0, counter.width, 10)),
                data(Int(0, data.width, 10)),
                wr_flag(Int(0, 1, 2)),
                done(Int(0, 1, 2)),
            ).Elif(AndList(start, Not(done)))(
                EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                component_ready(Int(1, 1, 2)),
                wr_en(Int(0, 1, 2)),
                If(Not(wr_available))(
                    component_ready(Int(0, 1, 2)),
                ).Elif(en)(
                    Case(din0[realdatawitdh - 2:realdatawitdh])(
                        When(Int(2, 2, 2))(  # Done = 2
                            component_ready(Int(1, 1, 2)),
                            If(wr_flag)(
                                wr_flag(Int(0, 1, 2)),
                                wr_data(data),
                                wr_en(Int(1, 1, 2)),
                            ).Else(
                                done(Int(1, 1, 2)),
                            )
                        ),
                        When(Int(1, 2, 2))(  # Valid = 1
                            If(counter == (data_width_ext // data_width) - 1)(
                                counter(Int(0, counter.width, 10)),
                                wr_flag(Int(0, 1, 2)),
                                # wr_data(Cat(din0[0:data_width], data[data_width:data_width_ext])),
                                # data(Cat(din0[0:data_width], data[data_width:data_width_ext])),
                                wr_data((din0[0:data_width] << (DATA_WIDTH * counter)) | data),
                                data(Int(0, data.width, 10)),
                                wr_en(Int(1, 1, 2)),
                            ).Else(
                                counter(counter + Int(1, counter.width, 10)),
                                data((din0[0:data_width] << (DATA_WIDTH * counter)) | data),
                                wr_flag(Int(1, 1, 2)),
                                # data(Cat(din0[0:data_width], data[data_width:data_width_ext])),
                            ),
                        )
                    )
                )
            )
        )

    return m


# Component that receives the input buffer BEGIN
def make_async_in(name, data_width, data_width_ext):
    m = Module(name)

    # basic signals BEGIN
    clk = m.Input('clk')
    rst = m.Input('rst')
    # en = m.Input('en')
    start = m.Input('start')
    rd_done = m.Input("rd_done")
    # basic signals END

    # fifo_in control BEGIN
    rd_available = m.Input('rd_available')
    rd_data = m.Input('rd_data', data_width_ext)
    rd_valid = m.Input('rd_valid')
    rd_en = m.OutputReg('rd_en')
    # fifo_in control END

    # control of data entry components BEGIN
    # component_ready = m.OutputReg('component_ready')
    # control of data entry components END

    # outputdata BEGIN
    reqr0 = m.OutputReg("reqr0")
    ackr0 = m.Input("ackr0")
    dout0 = m.OutputReg('dout0', data_width + 2)
    # outputdata END


    fsm_main = m.Reg('fsm_main', 3)
    FSM_IDLE = m.Localparam('FSM_IDLE', Int(0, fsm_main.width, 10))
    FSM_READ = m.Localparam('FSM_READ', Int(1, fsm_main.width, 10))
    FSM_DATA_OUT = m.Localparam('FSM_DATA_OUT', Int(2, fsm_main.width, 10))
    FSM_WAIT_ACK = m.Localparam('FSM_WAIT_ACK', Int(3, fsm_main.width, 10))
    FSM_DONE = m.Localparam('FSM_DONE', Int(4, fsm_main.width, 10))

    data = m.Reg('data', data_width_ext)
    counter = m.Reg('counter', 6)

    m.Always(Posedge(clk))(
        If(rst)(
            reqr0(Int(0, 1, 2)),
            rd_en(Int(0, 1, 2)),
            counter(Int(0, counter.width, 10)),
            # dout0(Int(0, dout0.width, 10)),
            fsm_main(FSM_IDLE),
        ).Elif(start)(
            rd_en(Int(0, 1, 2)),
            Case(fsm_main)(
                When(FSM_IDLE)(
                    If(rd_available)(
                        rd_en(Int(1, 1, 2)),
                        fsm_main(FSM_READ),
                    ).Elif(rd_done)(
                        dout0(Cat(Int(2, 2, 2), Int(0, data_width, 10))),
                        reqr0(Int(1, 1, 2)),
                        fsm_main(FSM_DONE),
                    )
                ),
                When(FSM_READ)(
                    If(rd_valid)(
                        data(rd_data),
                        counter(Int(0, counter.width, 10)),
                        # dout0(Int(0, dout0.width, 2)),
                        fsm_main(FSM_DATA_OUT)
                    )
                ),
                When(FSM_DATA_OUT)(
                    If(Not(ackr0))(
                        dout0(Cat(Int(1, 2, 10), data[0: data_width])),
                        reqr0(Int(1, 1, 2)),
                        data(data >> Int(data_width, 6, 10)),
                        counter(counter + Int(1, counter.width, 2)),
                        fsm_main(FSM_WAIT_ACK),
                    )
                ),
                When(FSM_WAIT_ACK)(
                    If(ackr0)(
                        reqr0(Int(0, 1, 2)),
                        If(counter >= (data_width_ext // data_width))(
                            fsm_main(FSM_IDLE),
                        ).Else(
                            fsm_main(FSM_DATA_OUT),
                        ),
                    )
                ),
                When(FSM_DONE)(
                    If(ackr0)(
                        reqr0(Int(0, 1, 2)),
                    ),
                    fsm_main(FSM_DONE),
                ),
            )
        )
    )

    return m


# Component the receives the input buffer END

# Component that records back data to the output buffer BEGIN
def make_async_out(name, data_width, data_width_ext):
    m = Module(name)
    realdatawitdh = data_width + 2

    # basic signals BEGIN
    clk = m.Input('clk')
    rst = m.Input('rst')
    start = m.Input('start')
    # en = m.Input("en")
    # basic signals END

    # fifo_out control BEGIN [1-1:0] afu_user_wr_en,
    wr_available = m.Input('wr_available')
    wr_en = m.OutputReg('wr_en')
    wr_data = m.OutputReg('wr_data', data_width_ext)
    # fifo_out control END

    # control of data entry components BEGIN
    # component_ready = m.OutputReg('component_ready')
    # control of data entry components END

    # inputdata BEGIN
    reql0 = m.Input("reql0")
    ackl0 = m.OutputReg("ackl0")
    din0 = m.Input('din0', realdatawitdh)
    # inputdata END

    # DONE signal
    done = m.OutputReg('done')

    DATA_WIDTH = m.Localparam("DATA_WIDTH", Int(data_width, data_width, 10))

    fsm_main = m.Reg('fsm_main', 3)
    FSM_IDLE = m.Localparam('FSM_IDLE', Int(0, fsm_main.width, 10))
    FSM_WAIT = m.Localparam('FSM_WAIT', Int(1, fsm_main.width, 10))
    FSM_DONE = m.Localparam('FSM_DONE', Int(2, fsm_main.width, 10))

    data = m.Reg('data', data_width_ext)
    counter = m.Reg('counter', 6)
    wr_flag = m.Reg("wr_flag")

    m.Always(Posedge(clk))(
        If(rst)(
            wr_en(Int(0, 1, 2)),
            ackl0(Int(0, 1, 2)),
            counter(Int(0, counter.width, 10)),
            data(Int(0, data.width, 10)),
            wr_flag(Int(0, 1, 2)),
            done(Int(0, 1, 2)),
            fsm_main(FSM_IDLE),
        ).Elif(AndList(start, wr_available))(
            wr_en(Int(0, 1, 2)),
            Case(fsm_main)(
                When(FSM_IDLE)(
                    If(reql0)(
                        fsm_main(FSM_WAIT),
                        ackl0(Int(1, 1, 2)),
                        Case(din0[realdatawitdh - 2:realdatawitdh])(
                            When(Int(1, 2, 2))(
                                If(counter == (data_width_ext // data_width) - 1)(
                                    counter(Int(0, counter.width, 10)),
                                    wr_flag(Int(0, 1, 2)),
                                    wr_data((din0[0:data_width] << (DATA_WIDTH * counter)) | data),
                                    data(Int(0, data.width, 10)),
                                    wr_en(Int(1, 1, 2)),
                                ).Else(
                                    counter(counter + Int(1, counter.width, 10)),
                                    data((din0[0:data_width] << (DATA_WIDTH * counter)) | data),
                                    wr_flag(Int(1, 1, 2)),
                                ),
                            ),
                            When(Int(2, 2, 2))(
                                If(wr_flag)(
                                    # wr_flag(Int(0, 1, 2)),
                                    wr_data(data),
                                    wr_en(Int(1, 1, 2)),
                                ),
                                fsm_main(FSM_DONE),
                            ),
                        ),
                    ),
                ),
                When(FSM_WAIT)(
                    If(Not(reql0))(
                        ackl0(Int(0, 1, 2)),
                        fsm_main(FSM_IDLE)
                    )
                ),
                When(FSM_DONE)(
                    If(Not(reql0))(
                        ackl0(Int(0, 1, 2)),
                    ),
                    done(Int(1, 1, 2)),
                    fsm_main(FSM_DONE),
                ),
            )
        )
    )

    return m


# Component that records back data to the output buffer END


def functions(functionname):
    funcs = {"SyncAdd": make_sync_add, "SyncAnd": make_sync_and, "SyncDiv": make_sync_div,
             "SyncMax": make_sync_max, "SyncMin": make_sync_min, "SyncMod": make_sync_mod, "SyncMul": make_sync_mul,
             "SyncOr": make_sync_or, "SyncShl": make_sync_shl, "SyncShr": make_sync_shr,
             "SyncSlt": make_sync_slt, "SyncSub": make_sync_sub, "SyncXor": make_sync_xor,
             "SyncIn": make_sync_in, "SyncOut": make_sync_out,
             "SyncAddI": make_sync_addi, "SyncAndI": make_sync_andi, "SyncDivI": make_sync_divi,
             "SyncModI": make_sync_modi, "SyncMulI": make_sync_muli, "SyncOrI": make_sync_ori,
             "SyncShlI": make_sync_shli, "SyncShrI": make_sync_shri, "SyncSltI": make_sync_slti,
             "SyncSubI": make_sync_subi,
             "SyncAbs": make_sync_abs, "SyncRegister": make_sync_register, "SyncNot": make_sync_not,
             "SyncKnnCtrl": make_sync_knnctrl, "SyncKnnQueue": make_sync_knnqueue,
             "AsyncIn": make_async_in, "AsyncOut": make_async_out,
             "AsyncGrn": make_async_grn}

    return funcs[functionname]

    '''FALTA IMPLEMENTAR nesta função
    "SyncAccAdd": makesyncadd,
    "SyncAccMax": makesyncadd,
    "SyncAccMin": makesyncadd,
    "SyncAccMul": makesyncadd,
    "SyncBeqI": makesyncadd,
    "SyncBeq": makesyncadd,
    "SyncBneI": makesyncadd,
    "SyncBne": makesyncadd,
    "SyncDemux": makesyncadd,
    "SyncFgate": makesyncadd,
    "SyncHistogram": makesyncadd,
    "SyncMerge": makesyncadd,
    "SyncMux": makesyncadd,
    "SyncTgate": makesyncadd
    '''


'''
SyncBeq.java
SyncBne.java

SyncBeqI.java
SyncBneI.java

SyncAccAdd.java
SyncAccMax.java
SyncAccMin.java
SyncAccMul.java

SyncHistogram.java

SyncMerge.java
SyncMux.java
SyncDemux.java


SyncFgate.java
SyncTgate.java
'''

# make_sync_in("in0", 32, 32).to_verilog("verilog")
# make_sync_out("out0", 32, 32).to_verilog("verilog")
# make_sync_knnctrl("knn", 32).to_verilog("verilog")
