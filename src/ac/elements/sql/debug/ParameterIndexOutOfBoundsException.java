/**
 * Title:        Overpower the PreparedStatement<p>
 * Description:  http://www.javaworld.com/javaworld/jw-01-2002/jw-0125-overpower.html<p>
 * Copyright:    Copyright (c) Troy Thompson Bob Byron<p>
 * Company:      JavaUnderground<p>
 * @author       Troy Thompson Bob Byron
 * @version 1.1
 */
package ac.elements.sql.debug;

import java.sql.SQLException;

public class ParameterIndexOutOfBoundsException extends SQLException {

    /**
     * 
     */
    private static final long serialVersionUID = 182128387818790320L;

    public ParameterIndexOutOfBoundsException() {
        super();
    }

    public ParameterIndexOutOfBoundsException(String s) {
        super(s);
    }
}