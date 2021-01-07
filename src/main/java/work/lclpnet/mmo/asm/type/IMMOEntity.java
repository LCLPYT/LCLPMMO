package work.lclpnet.mmo.asm.type;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import work.lclpnet.mmo.util.ClickListener;

public interface IMMOEntity<T extends Entity> {
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> IMMOEntity<T> get(T entity) {
		return (IMMOEntity<T>) entity;
	}
	
	/**
	 * Adds an event listener that is called when the entity is clicked.<br>
	 * 
	 * @param listener The listener.
	 * @param id The identifier of the listener
	 * @throws IllegalArgumentException when there was already a listener mapped to the specified id.
	 */
	void addClickListener(String id, ClickListener<T> listener);
	
	/**
	 * Removed a click listener.
	 * 
	 * @param id The identifier of the listener to remove.
	 */
	void removeClickListener(String id);
	
	/**
	 * Removes all click listeners
	 */
	void removeAllClickListeners();

	/**
	 * Called, when the entity was clicked.
	 * @param clicker The player who clicked the entity.
	 * @return true, if the click on the entity should be canceled.
	 */
	boolean onClick(PlayerEntity clicker);

}
