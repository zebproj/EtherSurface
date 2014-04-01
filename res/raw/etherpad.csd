/*
 EtherPad is a multi-touch synthesizer, using the Csound Android SDK for sound 
 generation.

 EtherPad heavily borrows code from the MultiTouchXY example, found in 
 the collection of Csound Android Examples provided in the Csound source code.
 
 The Csound Examples were created by Steven Yi and Victor Lazzarini in 2011.

 Copyright (C) 2014 Paul Batchelor

 This file is part of EtherPad.

 EtherPad is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
<CsoundSynthesizer>
<CsOptions>
-o dac -d -b512 -B2048
</CsOptions>
<CsInstruments>
nchnls=2
0dbfs=1
ksmps=32
sr = 44100

giscale ftgen 0, 0, 14, -2, \
0, 2, 4, 7, 9, 11, 12, 14, \
16, 19, 21, 24, 26, 28
gisine ftgen 0, 0, 4096, 10, 1
giadd ftgen 0, 0, 4096, 10, 1, 1, 1, 1
gicosine ftgen 2, 0, 4096, 11, 1

gienv ftgen 0, 0, 1024, 5, 1, 1024, 0.0001

giBP init 0

gadelL,gadelR init 0

gaL, gaR init 0

gisize init 8
gikey init 60
gioct init 4

gaMainL, gaMainR init 0
knum init 0
gascale init 0
gisound init 0

gisig ftgen	0,0, 257, 9, .5,1,270	; define a sigmoid, or better 

gipalamin ftgen 0, 0, 8192, -12, 20.0
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

ksend init 1

kvib oscili ky * 0.4, 6, gisine
if(giBP == 0) then
kmidi scale kx, 0, gisize
kmidi = int(kmidi)
knote tablei kmidi, giscale
knote_flat = knote + gikey + 12 * (gioct + 1)
knote = knote + gikey + kvib + 12 * (gioct + 1)
knote port knote, 0.03
knote_flat port knote_flat, 0.03
kcps= cpsmidinn(knote)
kcps_flat= cpsmidinn(knote_flat)
else
kstep scale kx, 0, gisize
kstep = int(kstep)
kstep port kstep, 0.03
kpow = 3^((kstep)/13)
print i(kpow)
kcps = (cpsmidinn(gikey + 12 * (gioct + 1) + kvib) * kpow)
kcps_flat = (cpsmidinn(gikey + 12 * (gioct + 1) ) * kpow)
endif

ktimb expcurve ky, 4

kindx = (cpsmidinn(60)/kcps) * 3
if(gisound == 0) then

a1 foscili ky * 0.05, kcps, 1, 1, ktimb * kindx, gisine
aenv linsegr 0, 0.5, 1, 1, 0
a1 = a1 * aenv 
a2 = a1

elseif(gisound == 1) then

asig vco2 ky * 0.05, kcps, 2, 0.5 * ktimb
a1 lpf18 asig, kcps * 4, 0.9 * ky, 10 + 15 * ky
a1 balance a1, asig
aenv linsegr 0, 0.005, 1, 0.5, 0
a1 = a1 * aenv
a2 = a1

elseif(gisound == 2) then

ishift      =           .00666667               ;shift it 8/1200.
ipch        =           cpspch(p4)              ;convert parameter 5 to cps.
ioct        =           octpch(p4)              ;convert parameter 5 to oct.
ksend = 0.5
kadsr       linsegr      0, 1, 1.0, 5, 0 ;ADSR envelope
kmodi       linsegr      0, 1, 5, 5, 0 ;ADSR envelope for I
kmodi		=			ky * 5
kmodr       linsegr      2.0, 15, 0.2              ;r moves from p6->p7 in p3 sec.
a1mod          =           kmodi*(kmodr-1/kmodr)/2
a1ndx       =           abs(a1mod*2/20)            ;a1*2 is normalized from 0-1.
a2mod          =           kmodi*(kmodr+1/kmodr)/2
a3          tablei      a1ndx, gipalamin, 1             ;lookup tbl in f3, normal index
ao1         oscili       a1mod, kcps_flat, gicosine             ;cosine
a4          =           exp(-0.5*a3+ao1)
ao2         oscili       a2mod*kcps_flat, kcps_flat, gicosine       ;cosine

kping_time	randomi		-9, 9, 4
kping_pan	oscili		0.5, 4, gisine
kping_pan	=	kping_pan + 0.5
aping		oscili		 1, kping_time * ky, gienv
asine		oscili		 0.1 * aping  * kadsr, kcps_flat, giadd
asineL, asineR pan2 asine, kping_pan
gadelL = gadelL + asineL * 0.2
gadelR = gadelR + asineR * 0.2
a1			oscili       .03*kadsr*a4, ao2+kcps_flat + cpsoct(ishift), gisine ;fnl outleft
a2			oscili       .03*kadsr*a4, ao2+kcps_flat - cpsoct(ishift), gisine ;fnl outright

a1 = a1 + asineL * ky
a2 = a2 + asineR * ky

endif

gaMainL = gaMainL + a1
gaMainR = gaMainR + a2
gadelL = gadelL + (a1 * (0.1 + 0.1 * (1 - ky)) * ksend)
gadelR = gadelR + (a1 * (0.1 + 0.1 * (1 - ky)) * ksend)
gaL = gaL + a1 * ((0.1 + 0.1 * (1 - ky)) * ksend)
gaR = gaR + a2 * ((0.1 + 0.1 * (1 - ky)) * ksend)
endin


instr 888 
adelL init 0
adelR init 0
adelL delay gadelL + adelL * 0.7, .8
adelR delay gadelR + adelR * 0.7, .8
adelL butlp adelL, 6000
adelR butlp adelR, 6000
gaMainL = gaMainL + adelL
gaMainR = gaMainR + adelR
clear gadelL, gadelR
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
giscale ftgen 0, 0, 14, -2, \
p4, p5, p6, p7,
p8, p9, p10, p11,
p12, p13, p14, p15,
p16, p17
endif

endin

instr 104;set sound
gisound = p4
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
i3 0 $INF
;i2 0 100 
</CsScore>
</CsoundSynthesizer>

