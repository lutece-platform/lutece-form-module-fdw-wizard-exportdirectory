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

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;


/**
 * Class for matching an entry from a directory to an entry from its copy
 *
 */
public class DirectoryEntryMatcher
{
    /**
     * Finds the the matching id entry in a duplicated directory for a given id
     * entry.
     * Matching is determined by the position of entries
     * @param nIdEntry the id entry to match
     * @param direcotryToCopy the original direcotry
     * @param direcotryCopy the duplicated direcotry
     * @param plugin the plugin
     * @return the matching id entry
     */
    public static int findMatchingIdEntry( int nIdEntry, Directory directoryToCopy, Directory directoryCopy,
        Plugin plugin )
    {
        int nMatchingIdEntry = DirectoryUtils.CONSTANT_ID_NULL;

        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( directoryToCopy.getIdDirectory(  ) );

        List<IEntry> listEntryToCopy = EntryHome.getEntryList( entryFilter, plugin );

        entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( directoryCopy.getIdDirectory(  ) );

        List<IEntry> listEntryCopy = EntryHome.getEntryList( entryFilter, plugin );

        int nSize = listEntryToCopy.size(  );

        for ( int i = 0; i < nSize; i++ )
        {
            IEntry entry = listEntryToCopy.get( i );

            if ( entry.getIdEntry(  ) == nIdEntry )
            {
                nMatchingIdEntry = listEntryCopy.get( i ).getIdEntry(  );

                break;
            }
        }

        return nMatchingIdEntry;
    }
}
