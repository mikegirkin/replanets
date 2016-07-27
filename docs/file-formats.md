[Player.RST](#player-rst)
[PDATAx.DAT](#pdata-dat)
[BDATAx.DAT](#bdata-dat)

<div id='player-rst'></div>
## Player.RST

 +0   8 DWORDs  Addresses of the file sections, each + 1
--- Host 3.20+ ---
+32   6 BYTEs   Signature "VER3.5" for Winplan RSTs.
+38   2 BYTEs   Sub-Version, currently either "00" or "01".
+40     DWORD   File position of the Winplan data, when existent.
+44     DWORD   Position of LEECHx.DAT, if existing (0 otherwise).
--- RST version "01" ---
+48     DWORD   Position of extended Ufo database

  Normally, the data follows at 96 (60h) or 33 (21h), with some
  uninitialized data between the header and the data.

  Winplan RSTs are sent if the host didn't get a TRN file, or if it got a
  Windows Turn file. Winplan RSTs contain a "DOS part" built up exactly
  as the normal DOS file. Windows RSTs contain additional information, see
  below. PHost remembers the RST file style over turns, and only sends
  Windows RSTs to registered clients.

  To make your program robust, you should check all file pointers before
  using them. Everything but the first 8 sections was hacked into the
  RST format later, and earlier hosts do not blank out those fields.
  THost-generated RSTs sometimes (rarely) bear the "VER3.5" signature
  without actually being Windows-style files. This is because THost does
  not initialize the space after the header, and if DOS happens to assign
  a cluster previously used by a Windows-style file, the signature will be
  still there. To guard against this case, double-check the validity of
  the Winplan part of the file: the "1120" and "1211" signatures look like
  good candidates. If you found the "VER3.5" signature, the Winplan header
  itself usually is valid, as it comes from a real RST file: checking the
  file pointers alone for plausible values (i.e., "non-negative", "not
  more than 2 MB", ...) does not buy you much in this case.


### Sections


  The 8 standard file sections have the following contents:

--- Section 1 (pointer at +0): Ships ---
 +0     WORD    Number of ships
 +2   n BYTEs   Ship records of 107 bytes each, see SHIPx.DAT

--- Section 2 (pointer at +4): Visual Contacts ---
 +0     WORD    Number of contacts. Must be <= 50 for Wisseman clients.
                VPHost and PHost with AllowBigTargets allow more than 50
                contacts if the client-side unpacker can handle those
                (VPUnpack, k-unpack, CCUnpack).
 +2   n BYTEs   Records of 34 bytes each, see TARGETx.DAT.
                DOS-style RSTs: the 50 nearest contacts.
                Windows-style RSTs: the first 50 contacts (in Id order)

--- Section 3 (pointer at +8): Planets ---
 +0     WORD    Number of planets
 +2   n BYTEs   Planet records of 85 bytes each, see PDATAx.DAT

--- Section 4 (pointer at +12): Bases ---
 +0     WORD    Number of starbases
 +2   n BYTEs   Starbase records of 156 bytes each, see BDATAx.DAT

--- Section 5 (pointer at +16): Messages ---
 +0     WORD    Number of messages
 +2   n BYTEs   Records of 6 bytes each
                 +0     DWORD   Address of message in RST file + 1
                 +4     WORD    Length of message in bytes
                The messages usually follow the message headers, and are
                encrypted like in MDATAx.DAT.
                Reportedly, Winplan cannot unpack results where the number
                of messages exceeds 5461, i.e., where the message directory
                is larger than 32k.

--- Section 6 (pointer at +20): Ship Coordinates ---
 +0   n BYTEs   500 (or 999) Records of 8 bytes each, see SHIPXYx.DAT:
                 +0     WORD    X position
                 +2     WORD    Y position
                 +4     WORD    Owner
                 +6     WORD    Mass in kt

--- Section 7 (pointer at +24): GENx.DAT ---
 +0  18 BYTEs   Timestamp
+18  88 BYTEs   11 score records of 8 bytes each
+106    WORD    Player Id
+108 20 BYTEs   Password
+128  3 DWORDs  Checksums of ship, planet, and base block
                These checksums are the sum of all unsigned bytes in all
                ship/planet/base records in the RST file. The first word
                of each section ("Number of...") is not counted. Note that
                these are not exactly the same checksums as in GENx.DAT.
+140    WORD    Turn number
+142    WORD    Timestamp checksum
                See GENx.DAT for more information on each field. Not all
                GENx.DAT fields are stored here, set the others to zero.

--- Section 8 (pointer at +28): VCRs ---
 +0     WORD    Number of VCRs
 +2   n BYTEs   VCR records of 100 bytes each, see VCRx.DAT.

  Unpack programs must generate two (nearly) identical copies of the ship,
  planet and base sections (.DAT and .DIS). Those files only differ in
  their signatures. The RST file does not contain the signatures.


### Detecting Host999

  There appears to be no signature telling whether you are dealing with
  a Host999 file or not. This only makes a difference for the SHIPXYx.DAT
  part which has no length specification. If section 1 or 2 contain an Id
  number greater than 500, you can be sure to have a Host999 RST (not for
  the scores or VCRs, those could have been changed by a 3rd party add on,
  since they have no other effect in a front-end program). You can use the
  fact that the Wisseman Host always places the sections in "correct"
  order, so if there are exactly 4000 bytes between sections 6 and 7, it's
  a "Host500" RST, if there are 7992, it's from Host999. Or, always assume
  you have 999 ships. If you then accidentally "run into" the GENx.DAT
  image: valid timestamps start with two numbers, which -- interpreted as
  a WORD -- yield an invalid or at least improbable X coordinate for a
  ship.

  Note that the standard Winplan999 and Unpack999 programs do not attempt
  to detect that, and blindly copy 7992 bytes, thus making bogus
  SHIPXYx.DAT files (see there) if Host999 is not used.


### "RST Format 36" / "RST Format 37"

  With the introduction of "FF allies" in Host 3.22.039, Tim announced the
  change to "RST Format 36" (or 37, depending on where you look).

  The basic RST file format did NOT change in these versions. Unpackers
  need not be changed. "RST Format 36" probably references the fact that
  allied targets are listed in TARGETx.DAT but not in SHIPXYx.DAT, making
  them invisible to DOS Planets. See the description of these two files for
  more details.


### Winplan data

  The pointer to this section is at offset +40.

 +0 500 RECORDs of 8 bytes each; Mine fields
                 +0     WORD    X
                 +2     WORD    Y
                 +4     WORD    Radius. Zero if the minefield has been
                                swept. All other information remains in
                                this minefield slot until the slot is
                                re-used for a new minefield.
                 +6     WORD    Owner.
                                 0      empty record
                                 1..11  Normal minefield belonging to
                                        race 1..11
                                 12     Crystalline Web mine field.
                                Note that this does not allow transmission
                                of web minefields that do not belong to the
                                Crystals. Non-Crystalline webs are sent as
                                normal mines (1..11).
                This section contains not only the minefields the player
                scanned this turn, but also all of his own minefields. This
                implicitly provides information about minefields that were
                swept completely.
 +? 600 BYTEs   Ion storms, 50 records of 12 bytes each
                 +0     WORD    X
                 +2     WORD    Y. Note that THost sometimes generates
                                storms with negative coordinates.
                 +4     WORD    Radius
                 +6     WORD    Voltage in MeV
                                 0      non-existent
                                 even   weakening storm
                                 odd    growing storm
                 +8     WORD    Warp
                +10     WORD    Heading in degrees
                see also GREY.HST below.
 +?  50 RECORDs of 4 bytes each: Explosions
                 +0     WORD    X (0 = non-existent)
                 +2     WORD    Y
                THost generates an explosion entry for every ship that
                exploded due to excess damage in battle or after a mine
                hit. PHost only generates these entries for ships
                exploding after a mine hit.
 +? 682 BYTEs   Contents of RACE.NM. RACE.NM remains unchanged if this
                field is empty (only spaces).
 +? 7800 BYTEs  Contents of UFO.HST, filtered
 +?   4 BYTEs   Signature "1211", if all visual contacts fit into the
                normal TARGETx.DAT file, "1120" otherwise. These numbers
                appear to be literals, not some strange flags.
--- Additional Visual Contacts ---
 +?     DWORD   Number of contacts.
 +?   n RECORDs of 34 bytes each.
                 +0     WORD    Id Number
                 +2     WORD    Owner
                 +4     WORD    Warp
                 +6     WORD    X location
                 +8     WORD    Y location
                +10     WORD    Type of hull
                +12     WORD    Heading in degrees, 0=North, 90=East,
                                180=South, 270=West, -1=unknown
                +14  20 BYTEs   Ship name, encrypted
                                  FOR i:=1 TO 20 DO
                                    Encrypted[i] := Original[i] XOR (155-i)
                Note that, except for the ship name, this is exactly a
                TARGETx.DAT record.


### LEECHx.DAT

  If the value at +44 is non-zero, it points to the following data:

 +0     DWORD   Size of LEECHx.DAT
 +4   n BYTEs   Contents of LEECHx.DAT

  You only need to place a file named LEECHx.DAT in the Host directory,
  and the Host will send it to the players. Winplan's UNPACK will unpack
  it to the game directory. This file is not used otherwise.


### Extended Ufo database

  In version "01" RSTs, the value at +48 points to the following data:

 +0     WORD    Total number of Ufos. The minimum value is 100, as there
                are already 100 Ufo records in the standard Winplan
                section.
 +2   n RECORDs of 78 bytes each: the Ufos. This field contains the Ufos
                from Id 101 onwards. Thus, the extended Ufo database stores
                100 Ufos less than specified in the WORD above.

<div id='pdata-dat'></div>
## PDATA.DAT

  This file contains all your planets, and all unowned planets you are
  orbiting (those for which you can request a mineral survey).

  Up to Host 3.14, you were getting PDATA records also for planets owned
  by enemies. This is problematic because they contain the friendly code,
  which current client programs, including recent Winplans, will happily
  display. Stealing minerals, sailing minefields -- it never was easier :)

  Up to PHost 3.4c, PHost was zeroing the fields Colonists, Supplies,
  Money, Mines, Factories, Defense, Colonist Tax, and Native Tax when
  you were scanning an unowned planet. Thus, the value zero actually
  means "don't know", not "zero".

 +0     WORD    Number of planets
 +2   n BYTEs   Records of 85 bytes each
 +m  10 BYTEs   PDATAx.DAT: Signature 2 -> GENx.DAT
                PDATAx.DIS: Signature 1

One Planet record:
 +0     WORD    Player Id, 0 for unowned planets
 +2     WORD    Planet Id
 +4   3 BYTEs   Friendly Code. THost permits ASCII codes 32 to 122.
 +7     WORD    Number of mines (0..~516)
 +9     WORD    Number of factories (0..~416)
+11     WORD    Number of defense posts (0..~366)
                The ranges are based upon the maximum population of 10
                million colonists (100000 clans). The maximum number of
                mines on a planet is (COLON = colonist clans):
                  COLON                        if COLON <= 200
                  ROUND(200+SQRT(COLON-200))   if COLON > 200
                Replace 200 with 100 for factories, and 50 for defenses.
                When building structures, the client program must subtract
                the appropriate costs from the available resources (1
                supply, and 4/3/10 mc for mines/factories/defenses).
+13     DWORD   Mined Neutronium
+17     DWORD   Mined Tritanium
+21     DWORD   Mined Duranium
+25     DWORD   Mined Molybdenum
+29     DWORD   Colonist clans (1 clan = 100 people). The THost limit
                is 100000 clans, the PHost limit is 250000 clans.
+33     DWORD   Supplies
+37     DWORD   Megacredits
                To sell supplies, remove the requested amount from the
                Supplies field and add it to the Megacredits field.
+41     DWORD   Neutronium in ground
+45     DWORD   Tritanium in ground
+49     DWORD   Duranium in ground
+53     DWORD   Molybdenum in ground
                        >4999           abundant
                        1200..4999      very common
                        600..1199       common
                        100..599        rare
                        1..99           very rare
                        0               none
+57     WORD    Neutronium density
+59     WORD    Tritanium density
+61     WORD    Duranium density
+63     WORD    Molybdenum density
                        70..100 large masses    "1 mine extracts 1 kt"
                        40..69  concentrated    "2 mines extract 1 kt"
                        30..39  dispersed       "3 mines extract 1 kt"
                        10..29  scattered       "5 mines extract 1 kt"
                        0..9    very scattered  "10 mines extract 1 kt"
                The figures on the right are from the docs, the actual
                amount of minerals extracted is
                - THost:
                  ERnd(ERnd(mines * density / 100) * miningrate / 100) * RF
                - PHost < 3.5, 4.1:
                  Trunc(Trunc(density * miningrate / 100) * RF * mines / 100)
                - PHost 3.5+, 4.1+:
                  Round(Round(density * miningrate / 100) * RF * mines / 100)
                where "miningrate" is the HConfig value, and RF it the
                "reptile factor" (2 if Reptilian natives, 1 otherwise).
+65     WORD    Colonist taxes (0..100)
+67     WORD    Native taxes (0..100)
                Note that THost limits taxation: when hissing, the maximum
                tax rate is 75%. Cyborgs can tax natives up to 20%.
+69     WORD    Colonist happiness (-300..100)
+71     WORD    Native happiness (-300..100)
                        90..100 happy
                        70..89  calm
                        50..69  unhappy
                        40..49  very angry
                        20..39  rioting
                        <20     fighting
                Tax collection possible for happiness>=30, population grows
                if >=70.
+73     WORD    Native Government (SPI = Socio Political Index)
                        0       none              0%
                        1       Anarchy          20%
                        2       Pre-Tribal       40%
                        3       Early-Tribal     60%
                        4       Tribal           80%
                        5       Feudal          100%
                        6       Monarchy        120%
                        7       Representative  140%
                        8       Participatory   160%
                        9       Unity           180%
+75     DWORD   Native clans (1 clan = 100 people). Maximum population
                somewhere around 150000 clans.
+79     WORD    Native Race
                        0       none
                        1       Humanoid
                        2       Bovinoid
                        3       Reptilian
                        4       Avian
                        5       Amorphous
                        6       Insectoid
                        7       Amphibian
                        8       Ghipsoldal
                        9       Siliconoid
+81     WORD    Temperature (Temperature in Fahrenheit = 100 - this value)
                        0..15   desert  (85..100°F)
                        16..35  tropical (65..84°F)
                        36..60  warm    (40..64°F)
                        61..85  cool    (15..39°F)
                        86..100 arctic  (0..14°F)
                In case you wonder why we store `100-F': originally, this
                was just a type code with no real-world equivalent. It was
                changed to mean a temperature with Host 3.20 and Winplan.
+83     WORD    1=Build base, 0 otherwise. The client program must subtract
                the starbase cost (900mc, 402T, 120D, 340M; configurable in
                PHost 4.0k+) from the available resources when setting this
                word to 1.

  The happiness change can be computed from the tax level and the other
  planet data. It corresponds to the value shown in PLANETS.EXE as
  follows:

     <= -6      Hate you
   -5 .. -1     Are angry at you
       0        Are undecided about you
   +1 .. +4     Like your leadership
     >= +5      Love you

  Formulas are different for PHost and THost.

  + Natives, THost:
    Trunc(500 + 50*SPI - 85*NativeTax - (Mines+Factories) DIV 2
              - Sqrt(Natives)) DIV 100
    ... plus 10 for Avians
  + Natives, PHost:
    Trunc(500 + 50*SPI - 85*NativeTax - (Mines+Factories) / 2
              - Sqrt(Natives)) DIV 100
    ... plus 10 for Avians
    (Note that the difference PHost/THost here is that THost uses integer
    division for industry effect while PHost does floating point).

  + Colonists, THost, Crystals with desert advantage:
    Trunc(1000 - 80*ColonistTax - Sqrt(ColonistClans)
               - (Mines+Factories) DIV 3 - 3*TempCode) DIV 100
  + Colonists, THost, other races:
    Trunc(1000 - 80*ColonistTax - Sqrt(ColonistClans)
               - (Mines+Factories) DIV 3 - 3*Abs(TempCode-50)) DIV 100
  + Colonists, PHost, Crystals with desert advantage:
    Trunc(1000 - 80*ColonistTax - Sqrt(ColonistClans)
               - (Mines+Factories) / 3 - TempCode/0.66) DIV 100
  + Colonists, PHost, other races:
    Trunc(1000 - 80*ColonistTax - Sqrt(ColonistClans)
               - (Mines+Factories) / 3 - Abs(TempCode-50)/0.33) DIV 100
    (Difference, again, is integer vs. floating point, and different
    handling of temperatures).


<div id='bdata-dat'></div>
## BDATA.DAT

 +0     WORD    Number of Starbases
 +2   n BYTEs   Records, 156 bytes each
 +m  10 BYTEs   BDATAx.DAT: Signature 2
                BDATAx.DIS: Signature 1

One Starbase Record:
 +0     WORD    Base Id (=Id of planet with this base)
 +2     WORD    Owner (1..11, BDATA.HST: 0=no base at this planet)
 +4     WORD    Defense (0..200)
                One starbase defense unit costs 10 mc and 1 Duranium.
 +6     WORD    Damage (0..100)
 +8     WORD    Engine Tech Level (1..10)
+10     WORD    Hulls Tech Level (1..10)
+12     WORD    Weapon Tech Level (1..10)
+14     WORD    Torpedo Tech Level (1..10)
                Tech upgrades: going from Tech N to N+1 costs 100*N mc.
+16   9 WORDs   Engines in storage (for the 9 engines from ENGSPEC.DAT)
+34  20 WORDs   Hulls in Storage. Each race has up to 20 possible hulls it
                can build. These 20 words correspond to the 20 TRUEHULL.DAT
                slots. There's no possibility to store hulls a player can't
                build normally.
+74  10 WORDs   Beam Weapons in storage
+94  10 WORDs   Torpedo Launchers in storage
+114 10 WORDs   Torpedoes in storage
                All the "storage" fields can hold values between 0 and 10000.
                As of Host 3.22.031, these fields are not allowed to be
                increased without sufficient tech. This prevents the cheat
                of building parts without tech, but also prevents the (legal)
                act of moving high-tech torps from a ship to a low-tech base.
+134    WORD    Number of fighters (0..60)
+136    WORD    Id of ship to be recycled/repaired
+138    WORD    What to do with that ship:
                 0      Nothing
                 1      Fix
                 2      Recycle
+140    WORD    Mission (Primary Order)
                 0      none
                 1      Refuel
                 2      Max Defense
                 3      Load Torps onto ships
                 4      Unload freighters
                 5      Repair base
                 6      Force a surrender
+142    WORD    Type of ship to build (0=no build order, 1..20=index into
                above array)
+144    WORD    Engine Type
+146    WORD    Beam Type
+148    WORD    Beam Count
+150    WORD    Torpedo Type
+152    WORD    Torpedo Count
+154    WORD    0. According to CPLAYER.BAS, this is a fighter count. When
                building a ship this value is effectively ignored, that's
                why PLANETS.EXE puts zero in here. This field should
                _really_ always be zero, after the build as many fighters
                as shown here are destroyed with no compensation...


### Build orders

  To build ship parts, ammo, tech levels or starbase defense, clients
  subtract the required resources from the planet and increase the
  starbase storage.

  To build a ship, you must buy the required items into storage; host will
  subtract them again. The build order must either be completely specified
  or completely zero. Beam Type and Beam Count are either both zero or both
  nonzero, likewise Torpedo Type and Count. Specifying a type but count
  zero is known to cause problems with various programs.

  Costs for a starbase fighter are 100 mc, 3 Tritanium and 2 Molybdenum;
  configurable in PHost 4.0k+.
