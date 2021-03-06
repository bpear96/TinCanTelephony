package net.medsouz.tct.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Gord360
 * @author medsouz
 * 
 */
@SideOnly(Side.CLIENT)
public class GuiOverlay extends GuiScreen {
	String username = Minecraft.getMinecraft().getSession().getUsername();
	protected GuiScreen oldScreen;
	private Window resetWindow = null;
	private ArrayList<Window> windows = new ArrayList<Window>();
	private Screen screen;

	public GuiOverlay(GuiScreen par1GuiScreen) {
		this.oldScreen = par1GuiScreen;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		this.buttonList.clear();
		windows.add(new WindowTest(this, "DEBUG_WINDOW 1 - Please Ignore", 100,150,170,50));
		windows.add(new WindowTest(this, "DEBUG_WINDOW 2 - Please Ignore", 100,50,170,50));
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char par1, int par2) {
		if(par2 == Keyboard.KEY_ESCAPE) {
			Minecraft.getMinecraft().currentScreen = oldScreen;
		}
	}
	
	Window getTopWindow(int mouseX, int mouseY){
		Window window = null;
		for(Window w : windows){
			if(mouseX > w.getX() && mouseX < w.getX() + w.getWidth() && mouseY > w.getY() - 16 && mouseY < w.getY() + w.getHeight()){
				window = w;
			}
		}
		return window;
	}
	
	protected void mouseClicked(int mouseX, int mouseY, int par3) {
		super.mouseClicked(mouseX, mouseY, par3);
		//for(int x = windows.size() - 1; x >= 0; x--) {
		for(int x = 0; x < windows.size(); x++){
			Window w = windows.get(x);
			if(getTopWindow(mouseX, mouseY) == w){
				resetWindow = w;
				if(mouseX > w.getX() && mouseX < w.getX() + w.getWidth() && mouseY > w.getY() - 16 && mouseY < w.getY()){
					draggedWindow = w;
					mouseXLast = mouseX;
					mouseYLast = mouseY;
				}
				for(GuiButton b : w.getButtonList()) {
					if (b.mousePressed(mc, mouseX, mouseY)){
						b.func_146113_a(mc.getSoundHandler());
						w.onButtonPress(b);
					}
				}
			}
		}
	}

	private int iconSpacing = 10;
	private int wordSpacing = 25;
	
	private Window draggedWindow = null;
	private int mouseXLast, mouseYLast;
	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float par3) {
		if(oldScreen != null) {
			oldScreen.drawScreen(0, 0, par3);
		}
		this.drawGradientRect(0, 0, width, height, -0x3FEFEFF0, -0x2FEFEFF0);
		//Background
		RenderHelper.drawBlockSide(2, 2, 0, height / 2 - 115, 50, 50, 1, 1);
		RenderHelper.drawBlockSide(2, 0, 0, height / 2 - 65, 50, 180, 1, 150 / 50);
		//Icons
		//Profile
		int off = -105;
		RenderHelper.drawImage(RenderHelper.downloadImage("https://minotar.net/avatar/"+username+"/32.png"), 9, height / 2 + off, 32, 32);
		off += wordSpacing;
		this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, "Profile", 25, height / 2 + off, 0xFFFFFF);
		//Friends
		off += iconSpacing;
		RenderHelper.drawParticle(80, 9, height / 2 + off, 32, 32);
		off += wordSpacing;
		this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, "Friends", 25, height / 2 + off, 0xFFFFFF);
		//Messages
		off += iconSpacing;
		RenderHelper.drawItemIcon(339, 9, height / 2 + off, 32, 32);
		off += wordSpacing;
		this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, "Messages", 25, height / 2 + off, 0xFFFFFF);
		//Groups
		off += iconSpacing;
		RenderHelper.drawItemIcon(267, 9, height / 2 + off, 32, 32);
		off += wordSpacing;
		this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, "Groups", 25, height / 2 + off, 0xFFFFFF);
		//Settings
		off += iconSpacing;
		RenderHelper.drawItemIcon(257, 9, height / 2 + off, 32, 32);
		off += wordSpacing;
		this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, "Settings", 25, height / 2 + off, 0xFFFFFF);
		//Servers
		off += iconSpacing;
		RenderHelper.drawItemIcon(399, 9, height / 2 + off, 32, 32);
		off += wordSpacing;
		this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, "Servers", 25, height / 2 + off, 0xFFFFFF);
		//RenderHelper.drawBlockSide(44, 2, 100, 90, 128, 16, 3, 0.5f);
		//RenderHelper.drawBlockSide(98, 0, 100, 106, 128, 128, 3, 3);
		
		// Draw screen
		if (screen != null) {
			RenderHelper.drawBlockSide(1, 0, 60, 20, width - 80, height - 40, (width - 80) / 50, (height - 40) / 50);
			screen.drawScreen(this, 60, 20, width - 80, height - 40);
		} else {//if there is no screen open then draw the windows
		
			//put the most recently moved window on top
			if(resetWindow != null){
				windows.remove(resetWindow);
				windows.add(windows.size(), resetWindow);
				resetWindow = null;
			}
			
			for(int x = windows.size() - 1; x >= 0; x--){//drag priority is the opposite of draw priority
				Window w = windows.get(x);
				if(Mouse.isButtonDown(0)) {
					/*if(draggedWindow == null) {
						if(mouseX > w.getX() && mouseX < w.getX() + w.getWidth() && mouseY > w.getY() - 16 && mouseY < w.getY()){
							draggedWindow = w;
							mouseXLast = mouseX;
							mouseYLast = mouseY;
						}
					}else*/ if (draggedWindow == w) {
						int diffX = mouseX - mouseXLast;
						int diffY = mouseY - mouseYLast;
						w.setPosition(w.getX() + diffX, w.getY() + diffY);
						mouseXLast = mouseX;
						mouseYLast = mouseY;
					}
				} else {
					draggedWindow = null;
				}
			}
			
			for(Window w : windows) {
				RenderHelper.drawBlockSide(44, 2, w.getX(), w.getY() - 16, w.getWidth(), 16, w.getWidth() / 50f, 0.5f);
				RenderHelper.drawBlockSide(98, 0, w.getX(), w.getY(), w.getWidth(), w.getHeight(), w.getWidth() / 50f, w.getHeight() / 50f);
				this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, w.getTitle(), w.getX() + w.getWidth() / 2, w.getY() - 12, 0xFFFFFF);
				w.drawWindowContents();
				int x = -1, y = -1;
				if(getTopWindow(mouseX, mouseY) == w) {//only give the real mouse position to top buttons, this prevents the back window from highlighting buttons.
					x = mouseX;
					y = mouseY;
				}
				for(GuiButton b : w.getButtonList()) {
					b.drawButton(mc, x, y);
					GL11.glColor3f(1f, 1f, 1f);//fix color leak
				}
			}
		}
		super.drawScreen(mouseX, mouseY, par3);
	}
	
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		if(oldScreen != null) {
			oldScreen.setWorldAndResolution(mc, width, height);
		}
		super.setWorldAndResolution(mc, width, height);
	}
	
	protected void actionPerformed(GuiButton b) {
		for(Window w : windows) {
			w.onButtonPress(b);
		}
	}
}