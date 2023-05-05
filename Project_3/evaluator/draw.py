import os
from PIL import Image

path = os.path.dirname(os.path.abspath(__file__))
imageId = "12074 (eval)_id_0.txt"
studentFolder = (
    path + "/student_segments/" + imageId
)  # you may have to specify the complete path

# original_path = ""

# Open the text file
with open(studentFolder, "r") as f:
    # Read the lines and split each line into a list of values
    lines = [line.strip().split(",") for line in f.readlines()]
    # Convert each value to an integer
    values = [int(value) for line in lines for value in line]

# Create an empty image
width = len(lines[0])
height = len(lines)
img = Image.new("L", (width, height))

# Set the pixel values
pixels = img.load()
for y in range(height):
    for x in range(width):
        pixels[x, y] = values[y * width + x]

# Display the image
img.show()
