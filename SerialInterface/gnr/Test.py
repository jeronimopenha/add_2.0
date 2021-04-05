from gnr.make_gnr_harp import make_gnr_harp
from gnr.readFile import readFile

path = 'benchmarks/Benchmark_5.txt'

functions = readFile(path)

make_gnr_harp(1, functions, 2 ** 3, 2 ** 12).to_verilog('Samples/gnr_rtl_188')
