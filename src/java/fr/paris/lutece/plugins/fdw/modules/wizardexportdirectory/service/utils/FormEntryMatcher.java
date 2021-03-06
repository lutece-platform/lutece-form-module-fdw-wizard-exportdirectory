/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.fdw.modules.wizardexportdirectory.service.utils;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;


/**
 * Class for matching an entry from a form to an entry from its copy
 * 
 */
public class FormEntryMatcher
{
    /**
     * Finds the the matching id entry in a duplicated form for a given id
     * entry.
     * Matching is determined by the position of entries
     * @param nIdEntry the id entry to match
     * @param formToCopy the original form
     * @param formCopy the duplicated form
     * @param plugin the plugin
     * @return the matching id entry
     */
    public static int findMatchingIdEntry( int nIdEntry, Form formToCopy, Form formCopy, Plugin plugin )
    {
        int nMatchingIdEntry = GenericAttributesUtils.CONSTANT_ID_NULL;

        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( formToCopy.getIdForm( ) );

        List<Entry> listEntryToCopy = EntryHome.getEntryList( entryFilter );

        entryFilter = new EntryFilter( );
        entryFilter.setIdResource( formCopy.getIdForm( ) );

        List<Entry> listEntryCopy = EntryHome.getEntryList( entryFilter );

        int nSize = listEntryToCopy.size( );

        for ( int i = 0; i < nSize; i++ )
        {
            Entry entry = listEntryToCopy.get( i );

            if ( entry.getIdEntry( ) == nIdEntry )
            {
                nMatchingIdEntry = listEntryCopy.get( i ).getIdEntry( );

                break;
            }
        }

        return nMatchingIdEntry;
    }
}
