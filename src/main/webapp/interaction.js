/* 
 * Copyright 2022 maber01.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

function selectAllNone( type )
{
  let filetablebody = document.getElementById( "filetablebody" );
  if ( !filetablebody )
    return;

  let checkboxes = filetablebody.getElementsByTagName( "input" );
  for ( let i = 0; i < checkboxes.length; i++ )
  {
    if ( type === "all" )
      checkboxes[i].checked = true;
    else if ( type === "invert" )
      checkboxes[i].checked = !checkboxes[i].checked;
    else
      checkboxes[i].checked = false;
  }
  
}

function selectAll()
{
  selectAllNone( "all" );
}

function selectNone()
{
  selectAllNone( "none" );
}

function selectInvert()
{
  selectAllNone( "invert" );
}

function selectRange()
{
  let filetablebody = document.getElementById( "filetablebody" );
  if ( !filetablebody )
    return;
  
  // find first and last selected rows...
  let checkboxes = filetablebody.getElementsByTagName( "input" );
  let first = -1;
  let last = -1;
  for ( let i = 0; i < checkboxes.length; i++ )
  {
    if ( first < 0 && checkboxes[i].checked )
    {
      first = i;
      break;
    }
  }
  for ( let i = checkboxes.length - 1; i >= 0 ; i-- )
  {
    if ( last < 0 && checkboxes[i].checked )
    {
      last = i;
      break;
    }
  }
  
  if ( first < 0 || (last-first) <= 1 )
  {
    alert( "Select at least two non-adjacent check boxes first to use range button. " );
    return;
  }
  
  for ( let i = first+1; i < last; i++ )
    checkboxes[i].checked =true;  
}

function sortFiles( f )
{
  if ( filemetadatalist.length < 2 )
    return;
  
  filemetadatalist.sort( f );
  console.log( 'Start list.' );
  for ( let i = 0; i<filemetadatalist.length; i++ )
    console.log( filemetadatalist[i].name );
  console.log( 'End list.' );
  
  let filetablebody = document.getElementById( "filetablebody" );
  if ( !filetablebody )
    return;
  
  let reference = document.getElementById( "filerow_" + filemetadatalist[filemetadatalist.length-1].id );
  for ( let i = filemetadatalist.length-2; i>=0; i-- )
  {
    let current = document.getElementById( "filerow_" + filemetadatalist[i].id );
    filetablebody.insertBefore( current, reference );
    reference = current;
  }
}

function sortByName()
{
  sortFiles( function( a, b ) { return a.name.localeCompare( b.name ); } );
}

function sortByLength()
{
  sortFiles( function( a, b ) { return a.length-b.length; } );
}

function sortByModified()
{
  sortFiles( function( a, b ) { return a.modified-b.modified; } );
}
