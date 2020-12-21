# Concurrent Star Wars
##### Demonstrates the usage of concurrency in Java by simulating an attack of Han Solo, C3PO, R2D2, Lando, and Leia, with the help of the mighty Ewoks.
##### Han Solo and C3PO command the Ewoks in the attacks, which are sent to them using a message bus by Princess Leia.
##### After they are done, they inform her and she in her turn sends R2D2 the command to deactivate the protective shield, so that Lando can go and perform the final bombing.

##### Developed for a System Programming course assignment in Ben-Gurion university.

### INPUT:
##### The input is a JSON file containing the description of each attack (including specific serial numbers for Ewoks needed for that attack), and the time it takes for each attack to be performed. Also, it is required to manually specify the maximum amount of Ewoks available.
###### - The serial numbers of the Ewoks run from 1 to the number specified in the "Ewoks" value in the JSON file.
###### - "attacks" value is a list of attack objects, which include the keys "serials" and "duration".
###### - The time it takes for R2D2 to deactivate the shield and for Lando to bomb is also specified in the input file.

### USAGE:
##### Using Maven, run:
```
$ mvn compile
$ mvn exec:java -Dexec.mainClass="concurrent_star_wars.mics.application.Main" -Dexec.args="path/to/input.json path/to/output.json"
```

### OUTPUT:
##### When finished, the program prints a short summary of the attack campaign, and outputs a JSON file according to the input provided.
