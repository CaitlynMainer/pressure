/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.Machine
import net.bdew.lib.multiblock.data.RSMode
import net.bdew.pressure.blocks.router.gui.{ContainerRouter, GuiRouter, MsgSetRouterSideControl}
import net.bdew.pressure.network.NetworkHandler
import net.minecraft.entity.player.EntityPlayer

object MachineRouter extends Machine("Router", BlockRouter) with GuiProvider {
  def guiId: Int = 3
  type TEClass = TileRouter

  if (FMLCommonHandler.instance().getSide.isClient)
    RouterIcons.init()

  NetworkHandler.regServerHandler({
    case (MsgSetRouterSideControl(side, mode), player) =>
      Misc.asInstanceOpt(player.openContainer, classOf[ContainerRouter]) map { container =>
        container.te.sideControl.set(side, mode)
      }
  })

  @SideOnly(Side.CLIENT)
  def getGui(te: TileRouter, player: EntityPlayer) = new GuiRouter(te, player)
  def getContainer(te: TileRouter, player: EntityPlayer) = new ContainerRouter(te, player)

  val rsModeOrder = Map(
    RSMode.ALWAYS -> RSMode.RS_ON,
    RSMode.RS_ON -> RSMode.RS_OFF,
    RSMode.RS_OFF -> RSMode.NEVER,
    RSMode.NEVER -> RSMode.ALWAYS
  )

}
