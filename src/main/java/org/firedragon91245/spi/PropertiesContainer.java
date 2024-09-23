/*
	original author: JavaZOOM, vorbisspi@javazoom.net, http://www.javazoom.net

	vorbisspi-repackaged - a modernized and modularized version of vorbisspi by javazoom
    Copyright (C) 2024 FireDragon91245

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
    USA
 */

package org.firedragon91245.spi;

import java.util.Map;

public interface PropertiesContainer
{
	public Map<String, Object> properties();
}
