jacg
====

Repository for the Java Attack Calculus Gear (Metin2 attack calculation tool)

The Java Attack Calculus Gear is the latest-generation tool for Attack and Damage calculation in Metin2.
It was created by Son Gohan in 2013 and exploits a bunch of game formulas discovered by Son Gohan, Mystikal
(former wiki.metin2.it staff) and some other Italian Metin2 players, particularly AlbyElite.

Installing
----------------
You just need to have Java SE7 or later; the JAR file is the standalone package containing the tool and
the weapon database. To use it, just double-click on jacg.jar, or type in the terminal:

java -jar /path/to/jacg.jar

Updating or editing the weapon database
-----------------------------------------
The weapon database is a simple text file contained in the JAR package. If you want to edit it, just
extract it from the archive with:

jar xvf jacg.jar it/metin2/wiki/weapondb.txt

(you must be in the same directory as jacg.jar). Once edited, re-archive it with

jar cvf jacg.jar it/metin2/wiki/weapondb.txt.

(the file weapondb.txt must be in the directory it/metin2/wiki/ relative to jacg.jar).

The database contains a table in the format:

NAME ATK_MIN[0-9] ATK_MAX[0-9] DIFFERENCE_ATK(MAX-MIN) ATK_SPEED ATK_GROWTH WEAPON_TYPE

where:
* ATK_MIN, ATK_MAX, ATK_SPEED and ATK_GROWTH consist of 10 columns with the values at each up;
* ATK_GROWTH at up X is defined as ATK_MAX(up=X) - ATK_MIN(up=X);
* TYPE is one of these:
1) SPADONE (2-handed sword)
2) SPADA (1-handed sword)
3) SPADA_SURA (1-handed sword only for SURA)
4) PUGNALE (dagger)
5) ARCO (bow)
6) CAMPANA (bell)
7) VENTAGLIO (fan)

You can put whole-line comments beginning the line with "#":
-- # this is a valid comment
-- [...] 3 10 34 # this is NOT a valid comment: "#" must begin the line!

Bugs
-------
Report bugs to son.gohan.mt2@gmail.com

Copying 
---------
You can clone this repository and edit the code (e.g. translate the tool), provided that you release your edited code with the same license as this (GNU GPL v3). Please refer to COPYING in the main directory.

Disclaimer
------------
THIS TOOL IS NOT RELATED WITH THE METIN2 STAFF (game, forum, wiki) NOR WITH GAMEFORGE, AND IT IS NOT OFFICIALLY SUPPORTED IN ANY WAY. THIS IS FREE SOFTWARE WITH ABSOLUTELY NO WARRANTY.
