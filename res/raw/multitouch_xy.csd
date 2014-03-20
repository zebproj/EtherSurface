<CsoundSynthesizer>
<CsOptions>
-o dac -d -b512 -B2048
</CsOptions>
<CsInstruments>
nchnls=2
0dbfs=1
ksmps=32
sr = 44100

giscale ftgen 0, 0, 8, -2, 0, 2, 4, 7, 9, 11, 12, 14
gisine ftgen 0, 0, 4096, 10, 1

gadel init 0

instr 2
a1 oscils 0.1, 400, 0
outs a1, a1
endin

instr 1	

i_instanceNum = p4
S_xName sprintf "touch.%d.x", i_instanceNum
S_yName sprintf "touch.%d.y", i_instanceNum
kx chnget S_xName
ky chnget S_yName

kx port kx, 0.01
ky port ky, 0.01

;kx = 1 - kx
kmidi scale kx, 0, 8
;;kfreq port kfreq, 0.05
;kamp chnget Samp
;kamp = 1 - kamp
;kamp port kamp, 0.1

kvib oscili ky * 0.4, 6, gisine
kmidi = int(kmidi)
knote tab kmidi, giscale
knote = knote + 60 + kvib
knote port knote, 0.03

a1 foscili ky * 0.1, cpsmidinn(knote), 1, 1, ky * 3, gisine
aenv linsegr 0, 0.5, 1, 1, 0
a1 = a1 * aenv
outs a1, a1
gadel = gadel + a1 * 0.2
endin


instr 888
adel init 0
adel delay gadel + adel * 0.7, .8
adel butlp adel, 6000
outs adel, adel
clear gadel
endin


</CsInstruments>
<CsScore>
f1 0 16384 10 1

i888 0 360000
;i2 0 100 
</CsScore>
</CsoundSynthesizer>

