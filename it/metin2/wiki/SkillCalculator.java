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

class SkillCalculator {

	// skills constant
	private static float[] k = { 	
		0.00f,
		0.05f,0.06f,0.08f,0.10f,0.12f,0.14f,0.16f,0.18f,0.20f,0.22f,
		0.24f,0.26f,0.28f,0.30f,0.32f,0.34f,0.36f,0.38f,0.40f,0.50f,
		0.52f,0.54f,0.56f,0.58f,0.60f,0.63f,0.66f,0.69f,0.72f,0.82f,
		0.85f,0.88f,0.91f,0.94f,0.98f,1.02f,1.06f,1.10f,1.15f,1.25f
	};

	/** Converts a string level (like 1, 10, M3, G7, P) into a number to use in formulae */
	private static byte parseLevel(String sLevel) throws IllegalArgumentException {
		try {
			byte level = Byte.parseByte(sLevel);
			if(level < 0 || level > 20) throw new IllegalArgumentException(sLevel);
			else return level;

		} catch(NumberFormatException e) {
			char prefix = sLevel.toLowerCase().charAt(0);
			if(prefix != 'm' && prefix != 'g' && prefix != 'p')
				throw new IllegalArgumentException(Character.toString(prefix));

			if(prefix == 'p') {
				if(sLevel.length() > 1) throw new IllegalArgumentException(sLevel);
				else return 40;
			}

			String subLevel = sLevel.substring(1);
			try {
				byte suffix = Byte.parseByte(subLevel);
				switch(prefix) {
					case 'm':
						return (byte)(20+suffix);
					case 'g':
						return (byte)(30+suffix);
				}
			} catch(NumberFormatException e2) {
				throw new IllegalArgumentException(sLevel);
			}
		}

		// hopefully will never happen
		throw new RuntimeException("Warning: parseLevel returned no value!");
	}

	public static int auraDellaSpada(String sLevel,int pgLiv,int pgStr) throws IllegalArgumentException {
		byte level = parseLevel(sLevel);
		if(level < 1) return 0;
		return (int)((100+3*pgLiv+pgStr)*k[level]);
	}
	
	public static int lamaMagica(String sLevel,int pgLiv,int pgInt) throws IllegalArgumentException {
		byte level = parseLevel(sLevel);
		if(level < 1) return 0;
		return (int)((3*pgInt+2*pgLiv)*k[level]);
	}

	public static int attaccoPiu(String sLevel,int pgInt) throws IllegalArgumentException {
		byte level = parseLevel(sLevel);
		if(level < 1) return 0;
		return (int)(5+(15+pgInt/5)*k[level]);
	}

	public static float frenzy(String sLevel) throws IllegalArgumentException {
		byte level = parseLevel(sLevel);
		if(level < 1) return 0;
		return (float)(((int)50*k[level])/2f);
	}

	public static float fear(String sLevel) throws IllegalArgumentException {
		byte level = parseLevel(sLevel);
		if(level < 1) return 0;
		return (float)(1+29*k[level]);
	}

	public static float blessing(String sLevel,int iq) {
		byte level = parseLevel(sLevel);
		if(level < 1) return 0;
		return (float)((4*k[level]+1)/(2*k[level]+3) * (0.3*iq+5));
	}

	public static float darkProtection(String sLevel,int iq) {
		byte level = parseLevel(sLevel);
		if(level < 1) return 0;
		return (float)((15+iq/2f)*k[level]);
	}
}
