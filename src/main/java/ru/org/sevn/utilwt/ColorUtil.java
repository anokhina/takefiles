/*******************************************************************************
 * Copyright 2017 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ru.org.sevn.utilwt;

import java.awt.Color;

public class ColorUtil {
    public static Color getColorByString(String str, Color def) {
		int delim = 255;
		if (str != null && str.length() > 0 ) {
			int hc = str.hashCode();
			if (hc < 0) hc *= -1;
			int b = hc % delim;
			hc = hc / delim;
			int g = hc % delim;
			hc = hc / delim;
			int r = hc % delim;
			return new Color(r, g, b);
		}
		return def;
    }
}
