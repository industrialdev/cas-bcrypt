/*
 * Copyright (c) 2015 Industrial https://industrialagency.ca/
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.wicket.cas;

import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.security.auth.login.FailedLoginException;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Class that given a table, username field and password field will query a
 * database table with the provided encryption technique to see if the user
 * exists. This class defaults to a PasswordTranslator of
 * PlainTextPasswordTranslator.
 *
 * @author Scott Battaglia
 * @author Dmitriy Kopylenko
 * @author Marvin S. Addison
 * @author Scott Burlington
 * @author Terry Appleby
 * @since 2015-11-27
 */

public class BCryptSearchModeSearchDatabaseAuthenticationHandler
    extends AbstractJdbcUsernamePasswordAuthenticationHandler
    implements InitializingBean {

    @NotNull
    private String fieldUser;

    private String fieldUserAlt;

    @NotNull
    private String fieldPassword;

    @NotNull
    private String tableUsers;

    private String sql;

    /**
     * {@inheritDoc}
     */
    @Override
    protected final HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential)
        throws PreventedException, FailedLoginException {

        final String username = credential.getUsername();
        final String password = credential.getPassword();
        final Map<String, Object> userDetails;

        try {
            if (fieldUserAlt.isEmpty()) {
                userDetails = getJdbcTemplate().queryForMap(this.sql, username);
            } else {
                userDetails = getJdbcTemplate().queryForMap(this.sql, username, username);
            }
        } catch (final DataAccessException e) {
            throw new PreventedException("SQL exception while executing query for " + username, e);
        }

        String encryptedPassword = (String) userDetails.get(fieldPassword);

        if (!isPasswordValid(password, encryptedPassword)) {
            throw new FailedLoginException("incorrect password specified for username " + username);
        }

        return createHandlerResult(credential, this.principalFactory.createPrincipal(username), null);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.sql = "SELECT " + fieldUser
            + "," + fieldPassword
            + " FROM " + this.tableUsers
            + " WHERE " + this.fieldUser + " = ?"
        ;

        if (!fieldUserAlt.isEmpty()) {
            this.sql += " OR " + this.fieldUserAlt + " = ?";
        }
    }

    /**
     * @param fieldPassword The fieldPassword to set.
     */
    public final void setFieldPassword(final String fieldPassword) {
        this.fieldPassword = fieldPassword;
    }

    /**
     * @param fieldUser The fieldUser to set.
     */
    public final void setFieldUser(final String fieldUser) {
        this.fieldUser = fieldUser;
    }

    /**
     * @param tableUsers The tableUsers to set.
     */
    public final void setTableUsers(final String tableUsers) {
        this.tableUsers = tableUsers;
    }

    /**
     * @param fieldUserAlt In case we need to include an alternate field for user lookup.
     */
    public void setFieldUserAlt(String fieldUserAlt) {
        this.fieldUserAlt = fieldUserAlt;
    }

    /**
     * Check password using BCrypt algorithm.
     *
     * @param plainTextPassword Plain text password.
     * @param encryptedPassword Known encrypted.
     * @return boolean isValid?
     */
    private boolean isPasswordValid(String plainTextPassword, String encryptedPassword) {
        return !(plainTextPassword == null
            || plainTextPassword.trim().length() == 0
            || encryptedPassword == null
            || encryptedPassword.trim().length() == 0
        ) && BCrypt.checkpw(plainTextPassword, encryptedPassword);
    }

}
