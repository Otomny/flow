package fr.omny.flow.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import lombok.Getter;

@Getter
public class DummyCommandSender implements CommandSender{

	private List<String> receivedMessages = new ArrayList<>();

	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
		return null;
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return null;
	}

	@Override
	public boolean hasPermission(String permission) {
		return false;
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return false;
	}

	@Override
	public boolean isPermissionSet(String permission) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void recalculatePermissions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public void setOp(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "DummyCommandSender";
	}

	@Override
	public Server getServer() {
		return null;
	}

	@Override
	public void sendMessage(String arg) {
		this.receivedMessages.add(arg);
	}

	@Override
	public void sendMessage(String... arg) {
		List.of(arg).forEach(this::sendMessage);
	}

	@Override
	public void sendMessage(UUID arg0, String arg1) {
		
	}

	@Override
	public void sendMessage(UUID arg0, String... arg1) {
		
	}

	@Override
	public Spigot spigot() {
		return null;
	}
	
}
