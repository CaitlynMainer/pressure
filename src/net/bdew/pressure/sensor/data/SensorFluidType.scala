/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.sensor.data

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.data.DataSlotTankBase
import net.bdew.lib.gui._
import net.bdew.lib.sensors.GenericSensorParameter
import net.bdew.pressure.sensor.{Icons, Sensors}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fluids._

import scala.reflect.ClassTag

case class FluidTypeParameter(fluid: Fluid) extends Sensors.SensorParameter {
  override val uid: String = "fluid." + fluid.getName
}

case class SensorFluidType[T: ClassTag](uid: String, iconName: String, accessor: T => DataSlotTankBase) extends Sensors.SensorType with Icons.Loader {
  override def defaultParameter: GenericSensorParameter = Sensors.DisabledParameter

  override def isValidParameter(p: GenericSensorParameter, obj: TileEntity): Boolean = p.isInstanceOf[FluidTypeParameter] || p == Sensors.DisabledParameter

  override def paramClicked(current: GenericSensorParameter, item: ItemStack, button: Int, mod: Int, obj: TileEntity): GenericSensorParameter = {
    if (button == 0 && mod == 0) {
      if (item == null) {
        Sensors.DisabledParameter
      } else if (FluidContainerRegistry.isContainer(item)) {
        val fluid = FluidContainerRegistry.getFluidForFilledItem(item)
        if (fluid != null && fluid.getFluid != null)
          FluidTypeParameter(fluid.getFluid)
        else
          current
      } else if (item.getItem.isInstanceOf[IFluidContainerItem]) {
        val fluid = item.getItem.asInstanceOf[IFluidContainerItem].getFluid(item)
        if (fluid != null && fluid.getFluid != null)
          FluidTypeParameter(fluid.getFluid)
        else
          current
      } else current
    } else current
  }

  override def loadParameter(tag: NBTTagCompound): GenericSensorParameter =
    Option(FluidRegistry.getFluid(tag.getString("fluid"))) map FluidTypeParameter getOrElse Sensors.DisabledParameter

  override def saveParameter(p: GenericSensorParameter, tag: NBTTagCompound): Unit = p match {
    case FluidTypeParameter(x) => tag.setString("fluid", x.getName)
    case _ =>
  }

  override def getResult(param: GenericSensorParameter, obj: TileEntity): Boolean = (obj, param) match {
    case (x: T, FluidTypeParameter(fluid)) =>
      val fs = accessor(x).getFluid
      fs != null && fs.getFluid == fluid
    case _ => false
  }

  @SideOnly(Side.CLIENT)
  override def drawSensor(rect: Rect, target: DrawTarget, obj: TileEntity): Unit = target.drawTexture(rect, texture)

  @SideOnly(Side.CLIENT)
  override def drawParameter(rect: Rect, target: DrawTarget, obj: TileEntity, param: GenericSensorParameter): Unit = param match {
    case FluidTypeParameter(fluid) =>
      if (fluid.getStillIcon != null)
        target.drawTexture(rect, Texture(Texture.BLOCKS, fluid.getStillIcon), Color.fromInt(fluid.getColor))
    case _ => target.drawTexture(rect, Sensors.DisabledSensor.texture)
  }

  override def getParamTooltip(obj: TileEntity, param: GenericSensorParameter): List[String] = {
    param match {
      case FluidTypeParameter(fluid) =>
        List(
          fluid.getLocalizedName(new FluidStack(fluid, 1)),
          EnumChatFormatting.GRAY + Misc.toLocal("pressure.sensor.fluid.tip.set") + EnumChatFormatting.RESET
        )
      case _ =>
        List(
          Sensors.DisabledParameter.localizedName,
          EnumChatFormatting.GRAY + Misc.toLocal("pressure.sensor.fluid.tip.unset") + EnumChatFormatting.RESET
        )
    }
  }
}
