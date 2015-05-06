package io.drakon.laundarray.net.msg

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.drakon.laundarray.Laundarray
import io.netty.buffer.ByteBuf

/**
 * Shiny~
 *
 * @author Arkan <arkan@drakon.io>
 */
public class MessageParticleEvent : IMessage, IMessageHandler<MessageParticleEvent, IMessage> {

    private var name: String = ""
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0

    public constructor() {}

    public constructor(name:String, x:Double, y:Double, z:Double) {
        this.name = name
        this.x = x
        this.y = y
        this.z = z
    }

    override fun fromBytes(buf: ByteBuf) {
        val nameLen = buf.readInt()
        name = String(buf.readBytes(nameLen).array())
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(name.length())
        buf.writeBytes(name.getBytes())
        buf.writeDouble(x)
        buf.writeDouble(y)
        buf.writeDouble(z)
    }

    override fun onMessage(message: MessageParticleEvent, ctx: MessageContext): IMessage? {
        Laundarray.getProxy().spawnParticle(message.name, message.x, message.y, message.z)
        return null
    }

}