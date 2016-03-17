**A Java library for easy generation of Minecraft worlds**

The fork differs from the master branch is the following ways:

1. Bug fix so the correct chunk coordinates are saved

2. Bug fix in calculation of height grid

3. Skylight calculation redone so that it runs at a practical speed for large maps. The time taken for a sample area  has dropped from 10 hours to 10 minutes.
This simplified model does not handle overhangs but works apart from that

4. Function to set a column of blocks on a single method call has been added.

5. The ability to handle large numbers of regions by using an LRU cache backed by saving the regions to disk.

6. Project has been Mavenised.
