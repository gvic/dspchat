public interface SharedObject_itf {
	
	public void lock_read();
	
	public void lock_write();
	
	public void unlock();
	
	public int getId();
	
	public Object getObj();

	public Object reduce_lock();

	public void invalidate_reader();

	public Object invalidate_writer();
}