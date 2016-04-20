import Image
import ImageFilter

sourcename = 'mus.standard.exp0.tif' #raw_input("enter source: ")
destname = 'out.jpg' #raw_input("enter destination file: ")

kernel5 = (1,4,7,4,1,4,16,26,16,4,7,26,41,26,7,4,16,26,16,4,1,4,7,4,1)
kernel3 = (0,1,0,1,4,1,0,1,0)
kernel_FUCK = (0,0,41,0,0,0,0,41,0,0,0,1,41,1,0,0,0,41,0,0,0,0,41,0,0)
kernel5_vert = (0,0,4,0,0,1,7,16,7,1,4,26,41,26,4,1,7,16,7,1,0,0,4,0,0)
kernel13_vert = (0,0,0,0,0,0,1,0,0,0,0,0,0,
                 0,0,0,0,0,1,4,1,0,0,0,0,0,
                 0,0,0,0,0,1,7,1,0,0,0,0,0,
                 0,0,0,0,1,4,16,4,1,0,0,0,0,
                 0,0,0,0,1,7,26,7,1,0,0,0,0,
                 0,0,0,1,4,16,41,16,4,1,0,0,0,
                 0,0,0,1,7,26,85,26,7,1,0,0,0,
                 0,0,0,1,4,16,41,16,4,1,0,0,0,
                 0,0,0,0,1,7,26,7,1,0,0,0,0,
                 0,0,0,0,1,4,16,4,1,0,0,0,0,
                 0,0,0,0,0,1,7,1,0,0,0,0,0,
                 0,0,0,0,0,1,4,1,0,0,0,0,0,
                 0,0,0,0,0,0,1,0,0,0,0,0,0)

source = Image.open(sourcename)
blur = source.filter(ImageFilter.Kernel((5,5), kernel_FUCK))


for i in range(0,100):
    blur = blur.filter(ImageFilter.Kernel((5,5), kernel_FUCK))


blurdata = list(blur.getdata())

for i in range(len(blurdata)):
    if blurdata[i][0] > 180:
        blurdata[i] = 0
    else:
        blurdata[i] = 255

blurmask = Image.new("L", blur.size, "black")
blurmask.putdata(blurdata)
blurmask.save("mask.jpg", "JPEG")
blur.save(destname, "JPEG")

blank = Image.new("L", blur.size, "white")
blobbed = Image.composite(source, blank, blurmask)
blobbed.save("blobbed.jpg", "JPEG")

'''
source = Image.open(sourcename)
target = Image.open(targetname)
mask = Image.open(maskname).convert("L")

g1source = source.filter(ImageFilter.Kernel((5,5), kernel5))
g2source = g1source.filter(ImageFilter.Kernel((5,5), kernel5))
g3source = g2source.filter(ImageFilter.Kernel((5,5), kernel5))

g1target = target.filter(ImageFilter.Kernel((5,5), kernel5))
g2target = g1target.filter(ImageFilter.Kernel((5,5), kernel5))
g3target = g2target.filter(ImageFilter.Kernel((5,5), kernel5))

g1sdata = g1source.getdata()
g2sdata = g2source.getdata()
g3sdata = g3source.getdata()

g1tdata = g1target.getdata()
g2tdata = g2target.getdata()
g3tdata = g3target.getdata()

d1sdata = []
d2sdata = []
d1tdata = []
d2tdata = []
for i in range(len(list(g1sdata))):
    r1 = g1sdata[i][0] - g2sdata[i][0] + 128
    g1 = g1sdata[i][1] - g2sdata[i][1] + 128
    b1 = g1sdata[i][2] - g2sdata[i][2] + 128
    r2 = g2sdata[i][0] - g3sdata[i][0] + 128
    g2 = g2sdata[i][1] - g3sdata[i][1] + 128
    b2 = g2sdata[i][2] - g3sdata[i][2] + 128
    d1sdata.append((r1,g1,b1))
    d2sdata.append((r2,g2,b2))

    r1 = g1tdata[i][0] - g2tdata[i][0] + 128
    g1 = g1tdata[i][1] - g2tdata[i][1] + 128
    b1 = g1tdata[i][2] - g2tdata[i][2] + 128
    r2 = g2tdata[i][0] - g3tdata[i][0] + 128
    g2 = g2tdata[i][1] - g3tdata[i][1] + 128
    b2 = g2tdata[i][2] - g3tdata[i][2] + 128
    d1tdata.append((r1,g1,b1))
    d2tdata.append((r2,g2,b2))

d1s = Image.new("RGB", source.size, "black")
d2s = Image.new("RGB", source.size, "black")
d1s.putdata(d1sdata)
d1s.convert("L").save("d1s.jpg", "JPEG")
d2s.putdata(d2sdata)
d2s.convert("L").save("d2s.jpg", "JPEG")

d1t = Image.new("RGB", source.size, "black")
d2t = Image.new("RGB", source.size, "black")
d1t.putdata(d1tdata)
d1t.convert("L").save("d1t.jpg", "JPEG")
d2t.putdata(d2tdata)
d2t.convert("L").save("d2t.jpg", "JPEG")

dest = Image.composite(source, target, mask)
dest.save(destname, "JPEG")
'''