import matplotlib.pyplot as plt

# Load data
data = {}

colors = [
    "b",
    "g",
    "r",
    "c",
    "m",
    "y",
    "olive",
    "navy",
    "teal",
    "salmon",
    "sienna",
    "slateblue",
    "darkcyan",
    "indigo",
    "steelblue",
    "chocolate",
    "peru",
    "forestgreen",
    "maroon",
    "goldenrod",
    "purple",
    "crimson",
    "tomato",
]
depot_color = "k"
patient_color = "bo"

# Plot patients
for key in data["Patients"]:
    plt.plot(data["Patients"][key]["x"], data["Patients"][key]["y"], patient_color)

# extract the coordinates of the depot
depot_x = data["Depot"]["x"]
depot_y = data["Depot"]["y"]

# plot the depot
plt.plot(depot_x, depot_y, marker="s", color=depot_color, markersize=10)

# iterate over the routes and plot each one
for i, route in enumerate(data["Routes"]):
    if len(data["Routes"][route]) > 0:
        x = (
            [depot_x]
            + [data["Patients"][str(j)]["x"] for j in data["Routes"][route]]
            + [depot_x]
        )
        y = (
            [depot_y]
            + [data["Patients"][str(j)]["y"] for j in data["Routes"][route]]
            + [depot_y]
        )
        plt.plot(x, y, color=colors[i % len(colors)])
    else:
        print(f"Route {i} is empty.")

# show the plot
plt.show()
