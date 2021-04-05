from veriloggen import *


# simple binary operators. BEGIN
def make_sync_add(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    din0 = m.Input('din0', realdatawitdh)
    din1 = m.Input('din1', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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
                              Mux((din0[0:data_width] > din1[0:data_width]), din0[0:data_width], din1[0:data_width])))
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

    m.Always(Posedge(clk), Posedge(rst))(
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
                              Mux((din0[0:data_width] < din1[0:data_width]), din0[0:data_width], din1[0:data_width])))
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

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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


# simple binary operators. END

# simple unary operators with immediate values. BEGIN
def make_sync_addi(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', dconf_width)
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    immediate = m.Reg('immediate', dconf_width + id_width)

    m.Always(Posedge(clk), Posedge(rst))(
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
    return m


def make_sync_andi(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', dconf_width)
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    immediate = m.Reg('immediate', dconf_width + id_width)

    m.Always(Posedge(clk), Posedge(rst))(
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
    return m


def make_sync_divi(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', dconf_width)
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    immediate = m.Reg('immediate', dconf_width + id_width)

    m.Always(Posedge(clk), Posedge(rst))(
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
    return m


def make_sync_modi(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', dconf_width)
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    immediate = m.Reg('immediate', dconf_width + id_width)

    m.Always(Posedge(clk), Posedge(rst))(
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
    return m


def make_sync_muli(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', dconf_width)
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    immediate = m.Reg('immediate', dconf_width + id_width)

    m.Always(Posedge(clk), Posedge(rst))(
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
    return m


def make_sync_ori(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', dconf_width)
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    immediate = m.Reg('immediate', dconf_width + id_width)

    m.Always(Posedge(clk), Posedge(rst))(
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
    return m


def make_sync_shli(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', dconf_width)
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    immediate = m.Reg('immediate', dconf_width + id_width)

    m.Always(Posedge(clk), Posedge(rst))(
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
    return m


def make_sync_shri(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', dconf_width)
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    immediate = m.Reg('immediate', dconf_width + id_width)

    m.Always(Posedge(clk), Posedge(rst))(
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
    return m


def make_sync_slti(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', dconf_width)
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    immediate = m.Reg('immediate', dconf_width + id_width)

    m.Always(Posedge(clk), Posedge(rst))(
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
    return m


def make_sync_subi(name, data_width):
    m = Module(name)
    realdatawitdh = data_width + 2
    id_width = 8
    dconf_width = 32

    ID = m.Parameter('ID', 1)
    IMMEDIATE = m.Parameter('IMMEDIATE', 1)

    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    dconf = m.Input('dconf', dconf_width)
    din0 = m.Input('din0', realdatawitdh)
    dout0 = m.OutputReg('dout0', realdatawitdh)

    immediate = m.Reg('immediate', dconf_width + id_width)

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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
                        Cat(Int(1, 2, 10), Mux(din0[data_width - 1], ((~din0[0:data_width]) + 1), din0[0:data_width])))
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

    m.Always(Posedge(clk), Posedge(rst))(
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

    m.Always(Posedge(clk), Posedge(rst))(
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


# simple unary operators values. End

# Component that receives the input buffer BEGIN
def make_sync_in(name, data_width, data_width_ext):
    m = Module(name)

    # basic signals BEGIN
    clk = m.Input('clk')
    rst = m.Input('rst')
    en = m.Input('en')
    start = m.Input('start')
    # basic signals END

    # fifo_in control BEGIN
    rd_available = m.Input('rd_available')
    rd_almost_empty = m.Input('rd_almost_empty')
    rd_data = m.Input('rd_data', data_width_ext)
    req_rd_data = m.OutputReg('req_rd_data')
    # fifo_in control END

    # control of data entry components BEGIN
    component_ready = m.Output('component_ready')
    # control of data entry components END

    # outputdata BEGIN
    dout0 = m.OutputReg('dout0', data_width + 2)
    # outputdata END

    FSM_START = m.Localparam('FSM_START', 0)
    FSM_IDLE = m.Localparam('FSM_IDLE', 1)
    FSM_READ = m.Localparam('FSM_READ', 2)
    FSM_DATA_OUT = m.Localparam('FSM_DATA_OUT', 3)
    FSM_DONE = m.Localparam('FSM_DONE', 4)

    data = m.Reg('data', data_width_ext)
    counter_total = m.Reg('counter_total', 64)
    counter = m.Reg('counter', 6)
    qtde_in = m.Reg('qtde_in', 64)

    fsm_main = m.Reg('fsm_main', 3)

    m.Always(Posedge(clk), Posedge(rst))(
        If(rst)(
            component_ready(Int(0, 1, 2)),
            req_rd_data(Int(0, 1, 2)),
            counter_total(Int(0, counter_total.width, 10)),
            counter(Int(0, counter.width, 10)),
            fsm_main(FSM_START),
        ).Elif(start)(
            req_rd_data(Int(0, 1, 2)),
            dout0(Int(0, dout0.width, 2)),
            component_ready(Int(0, 1, 2)),
            Case(fsm_main)(
                When(FSM_START)(
                    If(req_rd_data)(
                        qtde_in(data[0: 64]),
                        fsm_main(FSM_IDLE),
                    ).Elif(rd_available)(
                        req_rd_data(Int(1, 1, 2)),
                    ),
                ),
                When(FSM_IDLE)(
                    If(rd_available)(
                        req_rd_data(Int(1, 1, 2)),
                        fsm_main(FSM_READ),
                    )
                ),
                When(FSM_READ)(
                    data(rd_data),
                    counter(Int(0, counter.width, 10)),
                    component_ready(Int(1, 1, 2)),
                    fsm_main(FSM_DATA_OUT)
                ),
                When(FSM_DATA_OUT)(
                    component_ready(Int(1, 1, 2)),
                    If(en)(
                        dout0(Cat(Int(1, 2, 10), data[0: data_width])),
                        data(data >> Int(data_width, 6, 10)),
                        counter_total(counter_total + Int(1, counter_total.width, 2)),
                        counter(counter + Int(1, counter.width, 2)),
                    ),
                    If(counter_total == qtde_in - 1)(
                        fsm_main(FSM_DONE),
                    ).Elif(counter == (data_width_ext // data_width) - 1)(
                        fsm_main(FSM_IDLE),
                    ).Else(
                        fsm_main(FSM_DATA_OUT),
                    )
                ),
                When(FSM_DONE)(
                    dout0(Int(2, 2, 2), Int(0, data_width, 10)),
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
    # basic signals END

    # fifo_out control BEGIN
    wr_available = m.Input('wr_available')
    wr_almost_full = m.Input('wr_almost_full')
    wr_req_data = m.OutputReg('wr_req_data')
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

    FSM_READ = m.Localparam('FSM_READ', 0)
    FSM_DONE = m.Localparam('FSM_DONE', 1)

    data = m.Reg('data', data_width_ext)
    counter = m.Reg('counter', 6)

    fsm_main = m.Reg('fsm_main', 2)

    m.Always(Posedge(clk), Posedge(rst))(
        If(rst)(
            wr_req_data(Int(0, 1, 2)),
            component_ready(Int(0, 1, 2)),
            counter(Int(0, counter.width, 10)),
            done(Int(0, 1, 2)),
        ).Elif(AndList(start, Not(done)))(
            EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
            component_ready(Int(1, 1, 2)),
            wr_req_data(Int(0, 1, 2)),
            If(wr_almost_full)(
                component_ready(Int(0, 1, 2)),
            ),
            Case(din0[realdatawitdh - 2:realdatawitdh])(
                When(Int(2, 2, 2))(  # Done = 2
                    done(Int(1, 1, 2)),
                ), When(Int(1, 2, 2))(  # Valid = 1
                    If(counter == (data_width_ext // data_width) - 1)(
                        counter(Int(0, counter.width, 10)),
                        wr_data((data << data_width) | din0),
                        wr_req_data(Int(1, 1, 2)),
                    ).Else(
                        counter(counter + Int(1, counter.width, 10)),
                        data((data << data_width) | din0)
                    ),
                )
            )
        )
    )

    return m


# Component that records back data to the output buffer END


def functions(functionname):
    funcs = {"SyncAdd": make_sync_add, "SyncAnd": make_sync_and, "SyncDiv": make_sync_div,
             "SyncMax": make_sync_max, "SyncMin": make_sync_min, "SyncMod": make_sync_mod, "SyncMul": make_sync_mul,
             "SyncOr": make_sync_or, "SyncShl": make_sync_shl, "SyncShr": make_sync_shr,
             "SyncSlt": make_sync_slt, "SyncSub": make_sync_sub,
             "SyncIn": make_sync_in, "SyncOut": make_sync_out,
             "SyncAddI": make_sync_addi, "SyncAndI": make_sync_andi, "SyncDivI": make_sync_divi,
             "SyncModI": make_sync_modi, "SyncMulI": make_sync_muli, "SyncOrI": make_sync_ori,
             "SyncShlI": make_sync_shli, "SyncShrI": make_sync_shri, "SyncSltI": make_sync_slti,
             "SyncSubI": make_sync_subi,
             "SyncAbs": make_sync_abs, "SyncRegister": make_sync_register, "SyncNot": make_sync_not}

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
