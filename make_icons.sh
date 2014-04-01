function make_icon {
	inkscape -z -e ic_launcher.png -w $1 -h $1 icon.svg
	mv ic_launcher.png $2
}

make_icon 72 res/drawable-hdpi
make_icon 48 res/drawable-mdpi
make_icon 96 res/drawable-xhdpi
make_icon 144 res/drawable-xxhdpi
	
inkscape -z -e icon_hires.png -w 512 -h 512 icon.svg
