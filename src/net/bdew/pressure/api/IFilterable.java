/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.api;

import net.minecraftforge.fluids.Fluid;

public interface IFilterable {
    /**
     * Sets the fluid that will be accepted
     *
     * @param fluid new filter setting
     */
    public void setFluidFilter(Fluid fluid);

    /**
     * Removes the filter
     */
    public void clearFluidFilter();
}
