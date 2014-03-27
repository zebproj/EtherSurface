<CsoundSynthesizer>
<CsOptions>
-o dac -d -b512 -B2048
</CsOptions>
<CsInstruments>
nchnls=2
0dbfs=1
ksmps=32
sr = 44100

giscale ftgen 0, 0, 12, -2, \
0, 2, 4, 7, 9, 11, 12, 14, \
16, 19, 21, 24
gisine ftgen 0, 0, 4096, 10, 1

giBP init 0

gadel init 0

gaL, gaR init 0

gisize init 8
gikey init 60
gioct init 4

gaMainL, gaMainR init 0

instr 2
a1 oscils 0.1, 400, 0
;outs a1, a1
gaMainL = gaMainL + a1
endin

instr 1	

i_instanceNum = p4
S_xName sprintf "touch.%d.x", i_instanceNum
S_yName sprintf "touch.%d.y", i_instanceNum
Spulse sprintf "pulse.%d", i_instanceNum

kx chnget S_xName
ky chnget S_yName
;make scale match piano
kx = 1 - kx

kx port kx, 0.01
ky port ky, 0.01

;kx = 1 - kx
;;kfreq port kfreq, 0.05
;kamp chnget Samp
;kamp = 1 - kamp
;kamp port kamp, 0.1

kvib oscili ky * 0.4, 6, gisine
if(giBP == 0) then
kmidi scale kx, 0, gisize
kmidi = int(kmidi)
knote tablei kmidi, giscale
knote = knote + gikey + kvib + 12 * (gioct + 1)
knote port knote, 0.03
kcps= cpsmidinn(knote)
else
prints "we are here!"
kstep scale kx, 0, gisize
kstep = int (kstep)
kstep port kstep, 0.03
kpow = 3^((kstep)/13)
kcps = (cpsmidinn(gikey + 12 * (gioct + 1) + kvib) * kpow)
endif

ktimb expcurve ky, 4

a1 foscili ky * 0.05, kcps, 1, 1, ktimb * 3, gisine
aenv linsegr 0, 0.5, 1, 1, 0
a1 = a1 * aenv
;outs a1, a1
gaMainL = gaMainL + a1
gaMainR = gaMainR + a1
gadel = gadel + a1 * 0.1
gaL = gaL + a1 * 0.1
gaR = gaR + a1 * 0.1
endin


instr 888 
adel init 0
adel delay gadel + adel * 0.7, .8
adel butlp adel, 6000
gaMainL = gaMainL + adel
gaMainR = gaMainR + adel
clear gadel
endin


instr 999
aL, aR reverbsc gaL, gaR, 0.985, 10000
;outs aL, aR
gaMainL = gaMainL + aL
gaMainR = gaMainR + aR
clear gaL, gaR
endin

instr 100;set size
chnset p4, "size"
gisize = p4
endin

instr 101;set key
gikey = p4
endin

instr 102;set octave
gioct = p4
endin

instr 103;set scale type

if(p4 == -1) then
giBP = 1
else
giBP = 0
ftfree giscale, 0
giscale ftgen 0, 0, 12, -2, \
p4, p5, p6, p7,
p8, p9, p10, p11,
p12, p13, p14, p15
endif

endin

instr Mixer
aL clip gaMainL, 1, 1
aR clip gaMainR, 1, 1
outs aL, aR
clear gaMainL, gaMainR
endin

</CsInstruments>
<CsScore>
i888 0 $INF
i999 0 $INF
i"Mixer" 0 $INF
i100 0 0.5 8
i101 0 4 0

;i2 0 100 
</CsScore>
</CsoundSynthesizer>

