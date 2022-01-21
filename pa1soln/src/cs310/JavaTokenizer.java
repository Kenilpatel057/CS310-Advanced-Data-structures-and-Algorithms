package cs310;

// JavaTokenizer interface.
//
// ******************PUBLIC OPERATIONS***********************
// String getNextID( )        --> Get next Java identifier
// int getLineNumber( )       --> Return line number
// int getErrorCount( )       --> Return error count
// String skippedText( )      --> Return text skipped over on way to current ID or EOF

public interface JavaTokenizer
{
    /**
     * Gets current line number.
     * @return current line number.
     */
     int getLineNumber( );
     
    /**
     * Return next identifier, skipping comments
     * string constants, and character constants.
     * Place identifier in currentIdNode.word and return false
     * only if end of stream is reached.
     * @return next Java identifier
     */
     String getNextID( );
 	/**
 	 * Gets error count.
 	 * 
 	 * @return error count.
 	 */
 	public int getErrorCount();
 
     /**
      * Return text skipped over while looking for current id
      * @return string of text skipped over
      */
     String skippedText();
  
}

