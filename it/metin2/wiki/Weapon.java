/*
 * TACG: Theoretical Attack Calculus Gear
 * Copyright (C) 2013 Son Gohan (son.gohan.mt2@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.metin2.wiki;

class Weapon {

	public static enum WeaponType { SPADONE,SPADA,PUGNALE,ARCO,VENTAGLIO,CAMPANA };

	private String name;
	private short level;
	private short[][][] atk = new short[10][2][2];	//first index: up, second: phys/mag, third: min/max
	private short[] va = new short[10];
	private WeaponType type;
	
	private Weapon() {}

	public static Weapon getFromLine(String line) {
		if(line == null || line.length() == 0) return null;

		Weapon thisWea = new Weapon();
		String[] token = line.split("\\s+");
		try {
			if(token == null || token[0] == null || token[0].charAt(0) == '#') return null;
		} catch(IndexOutOfBoundsException e) {
			System.err.println("Index out of bounds for line: "+line);
			return null;
		}
		if(token.length != 43) {
			System.err.println("Error: invalid line "+line);
			System.err.println("Length is "+token.length+" (should be 43)");
			return null;
		}
		
		String[] nameAndLevel = token[0].split("\\(Liv_");
		if(nameAndLevel.length != 2) return null;
		thisWea.name = nameAndLevel[0].replaceAll("_"," ");
		try {
			if(nameAndLevel[1].split("\\-").length == 1)
				thisWea.level = Short.parseShort(nameAndLevel[1].replaceAll("\\)",""));
			else {
				/* Take only the minimum level for laziness */
				thisWea.level = Short.parseShort(nameAndLevel[1].replaceAll("\\)","").split("\\-")[0]);
			}
			// min atk
			for(int i = 1; i < 11; ++i) {
				thisWea.atk[i-1][0][0] = Short.parseShort(token[i]);
			}
			// max atk
			for(int i = 11; i < 21; ++i) {
				thisWea.atk[i-11][0][1] = Short.parseShort(token[i]);
			}
			// ignore diff (token[21])
			// atk speed
			for(int i = 22; i < 32; ++i) {
				thisWea.va[i-22] = Short.parseShort(token[i]);
			}
			// ignore growth (tokens[32~42])
			// type of weapon
			if(token[42].toLowerCase().equals("spadone"))
				thisWea.type = WeaponType.SPADONE;
			else if(token[42].toLowerCase().equals("spada"))
				thisWea.type = WeaponType.SPADA;
			else if(token[42].toLowerCase().equals("pugnale"))
				thisWea.type = WeaponType.PUGNALE;
			else if(token[42].toLowerCase().equals("arco"))
				thisWea.type = WeaponType.ARCO;
			else if(token[42].toLowerCase().equals("campana"))
				thisWea.type = WeaponType.CAMPANA;
			else if(token[42].toLowerCase().equals("ventaglio"))
				thisWea.type = WeaponType.VENTAGLIO;
			else return null;
		} catch(NumberFormatException e) { 
			System.err.println("Number format exception: ");
			e.printStackTrace();
			System.err.println("Your weapon database is corrupt.");
			return null;
		} catch(Exception e) {
			System.err.println("Caught exception in getFromLine: ");
			TACG.reportMsg(e);
		}

		return thisWea;
	}

	public String getName() { return name+" (Lv "+level+")"; }

	public short getAtk(int up,int type,int which) {
		return atk[up][type][which];
	}

	public short getVA(int up) {
		return va[up];
	}

	public WeaponType getType() {
		return type;
	}

	public short diffAtk() {
		if(atk[0][0][0] == 0 || atk[0][0][1] == 0) return (short)-1;
		else return (short)(atk[1][0][0] - atk[0][0][0]);
	}

	public short growth(int up) {
		return (short)(atk[up][0][0]-atk[0][0][0]);
	}

}
