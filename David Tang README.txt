I created a system where the user can click on separate buttons to display the different walking state images and text, and the UI for it as well.

MainActivity code
3 images, named running.jpg, still.jpg, walking.jpg, in the mipmap xhdpi folder
The strings in string.xml
ImageView Attributes:
	id - MovementImage
	layout_height - 256dp
	bottom_toBottomOf - parent
	left_toLeftOf - parent
	right_toRightOf - parent
TextView Attributes:
	id - MovementText
	bottom_toBottomOf - parent
	left_toLeftOf - parent
	right_toRightOf - parent
