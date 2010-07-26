	private int[] activeIndexes;

	public int[] getActiveIndexes() {
		if(activeIndexes == null) {
			String value = getActiveIndex();
			
			if(value == null || value.equals("")) {
				activeIndexes = new int[0];
			} 
			else {
				String indexes[] = value.split(",");
				activeIndexes = new int[indexes.length];
				
				for (int i = 0; i < indexes.length; i++) {
					activeIndexes[i] = Integer.parseInt(indexes[i].trim());
				}
			
			}
		
		}
		
		return activeIndexes;
	}
	
	public boolean isActive(int index) {
		for(int i : getActiveIndexes()) {
			if(i == index)
				return true;
		}
		
		return false;
	}