'''

from pyverilog.dataflow import dataflow_codegen
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] + din1_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] & din1_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] / din1_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10),
                              Mux((din0_reg[0:data_width] > din1_reg[0:data_width]), din0_reg[0:data_width],
                                  din1_reg[0:data_width])))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10),
                              Mux((din0_reg[0:data_width] < din1_reg[0:data_width]), din0_reg[0:data_width],
                                  din1_reg[0:data_width])))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] % din1_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] * din1_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] | din1_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] << din1_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] >> din1_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10),
                              Mux((din0_reg[0:data_width] < din1_reg[0:data_width]), Int(1, data_width, 10),
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] - din1_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)
    din1_reg = m.Reg("din1_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
        din1_reg(din1),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10))
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = {00,00}, Done = {10,10}, Valid = {01,01}'),
            Case(Cat(din0_reg[realdatawitdh - 2:realdatawitdh], din1_reg[realdatawitdh - 2:realdatawitdh]))(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Cat(Int(2, 2, 2), Int(2, 2, 2)))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Cat(Int(1, 2, 2), Int(1, 2, 2)))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] ^ din1_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    if (conf):
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] + immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] + IMMEDIATE[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    if (conf):
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] & immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] & IMMEDIATE[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    if (conf):
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] / immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] / IMMEDIATE[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    if (conf):
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] % immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] % IMMEDIATE[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    if (conf):
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] * immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] * IMMEDIATE[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    if (conf):
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] | immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] | IMMEDIATE[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    if (conf):
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] << immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] << IMMEDIATE[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    if (conf):
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] >> immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] >> IMMEDIATE[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    if (conf):
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10),
                                      Mux(din0_reg[0:data_width] < immediate[0:data_width], Int(1, data_width, 10),
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
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10),
                                      Mux(din0_reg[0:data_width] < IMMEDIATE[0:data_width], Int(1, data_width, 10),
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    if (conf):
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
                immediate(IMMEDIATE),
            ).Else(
                If(dconf[0:8] == ID)(
                    immediate(dconf[8:dconf_width])
                ),
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] - immediate[0:data_width]))
                        ), When()(  # I will consider this like STOP
                            dout0(Int(0, realdatawitdh, 10))
                        )
                    )
                )
            )
        )
    else:
        m.Always(Posedge(clk))(
            If(rst_reg)(
                dout0(Int(0, realdatawitdh, 10)),
            ).Else(
                If(en_reg)(
                    EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
                    Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                        When(Int(0, 4, 2))(  # Stop = 0
                            dout0(Int(0, realdatawitdh, 10))
                        ), When(Int(2, 2, 2))(  # Done = 2
                            dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                        ), When(Int(1, 2, 2))(  # Valid = 1
                            dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width] - IMMEDIATE[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10)),
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
            Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Int(2, 2, 2))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Int(1, 2, 2))(  # Valid = 1
                    dout0(
                        Cat(Int(1, 2, 10),
                            Mux(din0_reg[data_width - 1], ((~din0_reg[0:data_width]) + 1), din0_reg[0:data_width])))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10)),
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
            Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Int(2, 2, 2))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Int(1, 2, 2))(  # Valid = 1
                    dout0(Cat(Int(1, 2, 10), din0_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        din0_reg(din0),
    )

    m.Always(Posedge(clk))(
        If(rst_reg)(
            dout0(Int(0, realdatawitdh, 10)),
        ).Elif(en_reg)(
            EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
            Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                When(Int(0, 4, 2))(  # Stop = 0
                    dout0(Int(0, realdatawitdh, 10))
                ), When(Int(2, 2, 2))(  # Done = 2
                    dout0(Cat(Int(2, 2, 10), Int(0, data_width, 10)))
                ), When(Int(1, 2, 2))(  # Valid = 1
                    dout0(
                        Cat(Int(1, 2, 10), ~din0_reg[0:data_width]))
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    start_reg = m.Reg("start_reg")
    rd_available_reg = m.Reg("rd_available_reg")
    rd_data_reg = m.Reg("rd_data_reg", data_width_ext)
    rd_valid_reg = m.Reg("rd_valid_reg")
    rd_done_reg = m.Reg("rd_done_reg")

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        start_reg(start),
        rd_available_reg(rd_available),
        rd_data_reg(rd_data),
        rd_valid_reg(rd_valid),
        rd_done_reg(rd_done),
    )

    FSM_IDLE = m.Localparam('FSM_IDLE', 1)
    FSM_READ = m.Localparam('FSM_READ', 2)
    FSM_DATA_OUT = m.Localparam('FSM_DATA_OUT', 3)
    FSM_DONE = m.Localparam('FSM_DONE', 4)

    data = m.Reg('data', data_width_ext)
    counter = m.Reg('counter', 6)

    fsm_main = m.Reg('fsm_main', 3)

    m.Always(Posedge(clk))(
        If(rst_reg)(
            component_ready(Int(0, 1, 2)),
            rd_en(Int(0, 1, 2)),
            counter(Int(0, counter.width, 10)),
            fsm_main(FSM_IDLE),
        ).Elif(start_reg)(
            rd_en(Int(0, 1, 2)),
            component_ready(Int(0, 1, 2)),
            Case(fsm_main)(
                When(FSM_IDLE)(
                    If(rd_available_reg)(
                        rd_en(Int(1, 1, 2)),
                        fsm_main(FSM_READ),
                    ).Elif(AndList(rd_done_reg, Not(rd_available_reg)))(
                        fsm_main(FSM_DONE),
                    )
                ),
                When(FSM_READ)(
                    If(rd_valid_reg)(
                        data(rd_data_reg),
                        counter(Int(0, counter.width, 10)),
                        component_ready(Int(1, 1, 2)),
                        dout0(Int(0, dout0.width, 2)),
                        fsm_main(FSM_DATA_OUT)
                    )
                ),
                When(FSM_DATA_OUT)(
                    component_ready(Int(1, 1, 2)),
                    If(en_reg)(
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

    rst_reg = m.Reg("rst_reg")
    en_reg = m.Reg("en_reg")
    start_reg = m.Reg("start_reg")
    wr_available_reg = m.Reg("wr_available_reg")
    din0_reg = m.Reg("din0_reg", realdatawitdh)

    m.Always(Posedge(clk))(
        rst_reg(rst),
        en_reg(en),
        start_reg(start),
        wr_available_reg(wr_available),
        din0_reg(din0),
    )

    data = m.Reg('data', data_width_ext)
    counter = m.Reg('counter', 6)

    m.Always(Posedge(clk))(
        If(rst_reg)(
            wr_en(Int(0, 1, 2)),
            component_ready(Int(0, 1, 2)),
            counter(Int(0, counter.width, 10)),
            done(Int(0, 1, 2)),
        ).Elif(AndList(start_reg, Not(done)))(
            EmbeddedCode('//Stop = 00, Done = 10, Valid = 01'),
            component_ready(Int(1, 1, 2)),
            wr_en(Int(0, 1, 2)),
            If(Not(wr_available))(
                component_ready(Int(0, 1, 2)),
            ).Elif(en_reg)(
                Case(din0_reg[realdatawitdh - 2:realdatawitdh])(
                    When(Int(2, 2, 2))(  # Done = 2
                        done(Int(1, 1, 2)),
                        component_ready(Int(1, 1, 2)),
                    ),
                    When(Int(1, 2, 2))(  # Valid = 1
                        If(counter == (data_width_ext // data_width) - 1)(
                            counter(Int(0, counter.width, 10)),
                            wr_data(Cat(din0_reg[0:data_width], data[data_width:data_width_ext])),
                            data(Cat(din0_reg[0:data_width], data[data_width:data_width_ext])),
                            wr_en(Int(1, 1, 2)),
                        ).Else(
                            counter(counter + Int(1, counter.width, 10)),
                            data(Cat(din0_reg[0:data_width], data[data_width:data_width_ext])),
                        ),
                    )
                )
            )
        )
    )

    return m
'''