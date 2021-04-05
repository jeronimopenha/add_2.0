from veriloggen import *
from math import *

from make_fifo import make_fifo


def make_serial(serial_data_width):
    m = Module("serial")

    clk = m.Input("clk")
    rst = m.Input("rst")
    # Transmitter and Receiver ports
    rx = m.Input("rx")
    tx = m.Output("tx")
    rx_busy = m.Output("rx_busy")
    tx_busy = m.Output("tx_busy")

    # wr_data and rd_data fifos
    wr_available = m.Output("wr_available")
    wr_en = m.Input("wr_en")
    wr_data = m.Input("wr_data", serial_data_width)

    rd_available = m.Output("rd_available")
    rd_en = m.Input("rd_en")
    rd_data = m.Output("rd_data", serial_data_width)
    rd_data_valid = m.Output("rd_data_valid")

    m.EmbeddedCode("//fifo_in wires and regs")
    fifo_in_rd_en = m.Reg("fifo_in_rd_en")
    fifo_in_dout = m.Wire("fifo_in_dout", serial_data_width)
    fifo_in_empty = m.Wire("fifo_in_empty")
    fifo_in_almost_empty = m.Wire("fifo_in_almost_empty")
    fifo_in_full = m.Wire("fifo_in_full")
    fifo_in_almost_full = m.Wire("fifo_in_almost_full")
    fifo_in_count = m.Wire("fifo_in_count")
    fifo_in_valid = m.Wire("fifo_in_valid")

    m.EmbeddedCode("//fifo_out wires and regs")
    fifo_out_wr_en = m.Reg("fifo_out_wr_en")
    fifo_out_wr_data = m.Reg("fifo_out_wr_data", serial_data_width)
    fifo_out_empty = m.Wire("fifo_out_empty")
    fifo_out_almost_empty = m.Wire("fifo_out_almost_empty")
    fifo_out_full = m.Wire("fifo_out_full")
    fifo_out_almost_full = m.Wire("fifo_out_almost_full")
    fifo_out_count = m.Wire("fifo_out_count")

    m.EmbeddedCode("//baud_rate_gen wires")
    baud_rate_gen_rxclken = m.Wire("baud_rate_gen_rxclken")
    baud_rate_gen_txclken = m.Wire("baud_rate_gen_txclken")

    m.EmbeddedCode("//serial_receiver wires and regs")
    serial_receiver_rdy = m.Wire("serial_receiver_rdy")
    serial_receiver_data = m.Wire("serial_receiver_data", serial_data_width)
    serial_receiver_rst_parc = m.Reg("serial_receiver_rst_parc")

    m.EmbeddedCode("//serial_transmitter wires and regs")
    serial_transmitter_data = m.Reg("serial_transmitter_data", serial_data_width)
    serial_transmitter_wren = m.Reg("serial_transmitter_wren")

    wr_available.assign(~fifo_in_almost_full)
    rd_available.assign(~fifo_out_empty)

    m.EmbeddedCode("//data in control")
    m.Always(Posedge(clk))(
        serial_receiver_rst_parc(Int(0, 1, 2)),
        fifo_out_wr_en(Int(0, 1, 2)),
        If(AndList(serial_receiver_rdy, Not(serial_receiver_rst_parc)))(
            fifo_out_wr_data(serial_receiver_data),
            fifo_out_wr_en(Int(1, 1, 2)),
            serial_receiver_rst_parc(Int(1, 1, 2)),
        )

    )

    m.EmbeddedCode("//data out control")
    fsm_write = m.Reg("fsm_write", 2)
    FSM_WRITE_IDLE = m.Localparam("FSM_WRITE_IDLE", Int(0, fsm_write.width, 10))
    FSM_WRITE_WR_DATA = m.Localparam("FSM_WRITE_WR_DATA", Int(1, fsm_write.width, 10))

    m.Always(Posedge(clk))(
        If(rst)(
            fifo_in_rd_en(Int(0, 1, 2)),
            serial_transmitter_wren(Int(0, 1, 2)),
            fsm_write(FSM_WRITE_IDLE),
        ).Else(
            serial_transmitter_wren(Int(0, 1, 2)),
            fifo_in_rd_en(Int(0, 1, 2)),
            Case(fsm_write)(
                When(FSM_WRITE_IDLE)(
                    If(AndList(Not(fifo_in_empty), Not(tx_busy), Not(serial_transmitter_wren)))(
                        fifo_in_rd_en(Int(1, 1, 2)),
                        fsm_write(FSM_WRITE_WR_DATA),
                    )
                ),
                When(FSM_WRITE_WR_DATA)(
                    If(fifo_in_valid)(
                        serial_transmitter_data(fifo_in_dout),
                        serial_transmitter_wren(Int(1, 1, 2)),
                        fsm_write(FSM_WRITE_IDLE),
                    )
                ),
            )
        )
    )

    fifo = make_fifo()
    # FIFO_IN
    con = [("clk", clk), ("rst", rst), ("we", wr_en), ("din", wr_data), ("re", fifo_in_rd_en), ("dout", fifo_in_dout),
           ("empty", fifo_in_empty), ("almostempty", fifo_in_almost_empty), ("full", fifo_in_full),
           ("almostfull", fifo_in_almost_full), ("count", fifo_in_count), ("valid", fifo_in_valid)]
    params = [("FIFO_WIDTH", 8), ("FIFO_DEPTH_BITS", 3)]
    m.Instance(fifo, "fifo_in", params, con)

    # FIFO_OUT
    con = [("clk", clk), ("rst", rst), ("we", fifo_out_wr_en), ("din", fifo_out_wr_data), ("re", rd_en),
           ("dout", rd_data), ("empty", fifo_out_empty), ("almostempty", fifo_out_almost_empty),
           ("full", fifo_out_full), ("almostfull", fifo_out_almost_full), ("count", fifo_out_count),
           ("valid", rd_data_valid)]
    params = [("FIFO_WIDTH", 8), ("FIFO_DEPTH_BITS", 3)]
    m.Instance(fifo, "fifo_out", params, con)

    # serialbaudrategenerator
    baud_rate_generator = make_baud_rate_gen()
    params = []
    con = [("clk", clk), ("rxclken", baud_rate_gen_rxclken), ("txclken", baud_rate_gen_txclken)]
    m.Instance(baud_rate_generator, "baud_rate_generator", params, con)

    # serial_receiver
    serial_receiver = make_serial_receiver()
    params = []
    con = [("clk", clk), ("rx", rx), ("rdyclr", serial_receiver_rst_parc), ("clken", baud_rate_gen_rxclken),
           ("rdy", serial_receiver_rdy),
           ("data", serial_receiver_data), ("rxbusy", rx_busy)]
    m.Instance(serial_receiver, "serial_receiver", params, con)

    # serial_transmitter
    serial_transmitter = make_serial_transmitter()
    params = []
    con = [("clk", clk), ("din", serial_transmitter_data), ("wren", serial_transmitter_wren),
           ("clken", baud_rate_gen_txclken),
           ("tx", tx), ("txbusy", tx_busy)]
    m.Instance(serial_transmitter, "serial_transmitter", params, con)

    initial = m.Initial()
    initial.add(fifo_in_rd_en(Int(0, 1, 2)))
    initial.add(serial_transmitter_wren(Int(0, 1, 2)))
    initial.add(fsm_write(Int(0, fsm_write.width, 2)))

    return m


def make_baud_rate_gen():
    clkfreq = 50000000
    rxtxbps = 57600#115200

    m = Module("baud_rate_gen")
    clk = m.Input("clk")
    rxclken = m.Output("rxclken")
    txclken = m.Output("txclken")

    RX_ACCMAX = clkfreq // (rxtxbps * 16)
    TX_ACCMAX = clkfreq // rxtxbps
    rxacc = m.Reg("rxacc", ceil(log2(clkfreq / (rxtxbps * 16))))
    txacc = m.Reg("txacc", ceil(log2(clkfreq / rxtxbps)))

    rxclken.assign(rxacc == Int(0, rxacc.width, 10))
    txclken.assign(txacc == Int(0, txacc.width, 10))

    m.Always(Posedge(clk))(
        If(rxacc == RX_ACCMAX)(
            rxacc(Int(0, rxacc.width, 10)),
        ).Else(
            rxacc(rxacc + Int(1, rxacc.width, 10)),
        ),
        If(txacc == TX_ACCMAX)(
            txacc(Int(0, txacc.width, 10)),
        ).Else(
            txacc(txacc + Int(1, txacc.width, 10)),
        )
    )
    return m


def make_serial_receiver():
    serial_data_width = 8

    m = Module("serial_receiver")
    rx = m.Input("rx")
    rdyclr = m.Input("rdyclr")
    clk = m.Input("clk")
    clken = m.Input("clken")
    rdy = m.OutputReg("rdy")
    data = m.OutputReg("data", serial_data_width)
    rxbusy = m.Output("rxbusy")

    initial = m.Initial()
    initial.add(rdy(Int(0, 1, 2)))
    initial.add(data(Int(0, serial_data_width, 10)))

    fsm_receiver = m.Reg("fsm_receiver", 2)
    RXSTATE_START = m.Localparam("RXSTATE_START", Int(0, fsm_receiver.width, 10))
    RXSTATE_DATA = m.Localparam("RXSTATE_DATA", Int(1, fsm_receiver.width, 10))
    RXSTATE_STOP = m.Localparam("RXSTATE_STOP", Int(2, fsm_receiver.width, 10))

    sample = m.Reg("sample", 4)
    bitpos = m.Reg("bitpos", 4)
    scratch = m.Reg("scratch", 8)

    rxbusy.assign(fsm_receiver != RXSTATE_START)

    m.Always(Posedge(clk))(
        If(rdyclr)(
            rdy(Int(0, 1, 2)),
        ),
        If(clken)(
            Case(fsm_receiver)(
                When(RXSTATE_START)(
                    # Start counting from the first low sample, once we've
                    # sampled a full bit, start collecting data bits.
                    If(OrList(Not(rx), sample != Int(0, sample.width, 10)))(
                        sample(sample + Int(1, sample.width, 10)),
                    ),
                    If(sample == Int(15, sample.width, 10))(
                        bitpos(Int(0, bitpos.width, 10)),
                        sample(Int(0, sample.width, 10)),
                        scratch(Int(0, scratch.width, 10)),
                        fsm_receiver(RXSTATE_DATA),
                    )
                ),
                When(RXSTATE_DATA)(
                    sample(sample + Int(1, sample.width, 10)),
                    If(sample == Int(8, sample.width, 10))(
                        scratch[bitpos](rx),
                        bitpos(bitpos + Int(1, bitpos.width, 10)),
                    ),
                    If(AndList(bitpos == 8, sample == 15))(
                        fsm_receiver(RXSTATE_STOP),
                    )
                ),
                When(RXSTATE_STOP)(
                    # Our baud clock may not be running at exactly the
                    # same rate as the transmitter.  If we thing that
                    # we're at least half way into the stop bit, allow
                    # transition into handling the next start bit.
                    If(OrList(sample == 15, AndList(sample >= 8, Not(rx))))(
                        data(scratch),
                        rdy(Int(1, 1, 2)),
                        sample(Int(0, sample.width, 10)),
                        fsm_receiver(RXSTATE_START),
                    ).Else(
                        sample(sample + Int(1, sample.width, 10)),
                    )
                ),
                When()(
                    fsm_receiver(RXSTATE_START),
                )
            )
        )
    )
    return m


def make_serial_transmitter():
    serial_data_width = 8

    m = Module("serial_transmitter")
    din = m.Input("din", serial_data_width)
    wren = m.Input("wren")
    clk = m.Input("clk")
    clken = m.Input("clken")
    tx = m.OutputReg("tx")
    txbusy = m.Output("txbusy")

    initial = m.Initial()
    initial.add(tx(Int(1, 1, 2)))

    fsm_transmiter = m.Reg("fsm_transmiter", 2)
    TXSTATE_IDLE = m.Localparam("TXSTATE_IDLE", Int(0, fsm_transmiter.width, 10))
    TXSTATE_START = m.Localparam("TXSTATE_START", Int(1, fsm_transmiter.width, 10))
    TXSTATE_DATA = m.Localparam("TXSTATE_DATA", Int(2, fsm_transmiter.width, 10))
    TXSTATE_STOP = m.Localparam("TXSTATE_STOP", Int(3, fsm_transmiter.width, 10))

    data = m.Reg("data", 8)
    bitpos = m.Reg("bitpos", 3)

    txbusy.assign(fsm_transmiter != TXSTATE_IDLE)

    m.Always(Posedge(clk))(
        Case(fsm_transmiter)(
            When(TXSTATE_IDLE)(
                If(wren)(
                    data(din),
                    bitpos(Int(0, bitpos.width, 10)),
                    fsm_transmiter(TXSTATE_START),
                )
            ),
            When(TXSTATE_START)(
                If(clken)(
                    tx(Int(0, 1, 2)),
                    fsm_transmiter(TXSTATE_DATA),
                )
            ),
            When(TXSTATE_DATA)(
                If(clken)(
                    If(bitpos == Int(7, bitpos.width, 10))(
                        fsm_transmiter(TXSTATE_STOP),
                    ).Else(
                        bitpos(bitpos + Int(1, bitpos.width, 10)),
                    ),
                    tx(data[bitpos]),
                )
            ),
            When(TXSTATE_STOP)(
                If(clken)(
                    tx(Int(1, 1, 2)),
                    fsm_transmiter(TXSTATE_IDLE),
                )
            ),
            When()(
                tx(Int(1, 1, 2)),
                fsm_transmiter(TXSTATE_IDLE),
            )
        )
    )

    return m
