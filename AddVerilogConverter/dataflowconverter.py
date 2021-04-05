from makedataflow import makedataflow
from makeComponents import *

#hds = "/home/jeronimo/Dropbox/NetBeansProjects/add/ADD_Examples/src/sync/design/sumTest.hds"
hds = "/home/jeronimo/Área de Trabalho/teste/teste.hds"
verilogPath = "verilog"
datawidthext = 512

m = makedataflow(hds, datawidthext)
