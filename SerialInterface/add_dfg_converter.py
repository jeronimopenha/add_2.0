from make_add_hw import make_add_hw, make_dfg_only

# hds = "/home/jeronimo/Dropbox/NetBeansProjects/add/ADD_Examples/src/sync/design/sumTest.hds"
#hds = "/home/jeronimo/Área de Trabalho/teste/kaio.hds"
#hds = "/home/jeronimo/Área de Trabalho/teste/knn.hds"
#hds = "/home/jeronimo/Área de Trabalho/teste/teste2.hds"
#hds = "/home/jeronimo/Área de Trabalho/teste/asyncloopback.hds"
hds = "/home/jeronimo/Área de Trabalho/teste/asyncgnr.hds"
verilogPath = "verilog"
data_width_ext = 512
serial_data_width = 8
# mercurioiv, de2cyclone2, de2115,other
#board = "mercurioiv"

#make_add_hw(data_width_ext, serial_data_width, hds, board).to_verilog(verilogPath)

dfgs = make_dfg_only(data_width_ext, hds)

for dfg in dfgs:
    dfg.to_verilog("verilog")