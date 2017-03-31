AJS.toInit(function () {
  AJS.$( ".mm-title" ).click( function() {
    AJS.$( "> .mm-fragment-details", this.parentNode.parentNode ).slideToggle( "fast" );
    return false;
  });
  
  AJS.$( ".mm-more-details" ).click( function() {
    AJS.$( "> .mm-fragment-details", this.parentNode.parentNode.parentNode ).slideToggle( "fast" );
    return false;
  });
  
  var allFragments = AJS.$( ".mm-fragment-details" ) 
  
  AJS.$( ".mm-expand-all" ).click( function() {
    allFragments.show();
    return false;
  });
  
  AJS.$( ".mm-contract-all" ).click( function() {
    allFragments.hide();
    return false;
  });

  // Enable/Disable highlighting
  var selects = AJS.$( ".mm-locations select" );
  selects.click( function() {
    // Cancel click events so it doesn't trigger an expand/collapse.
    return false;
  });
  selects.change( function() {
    // Change the display class
    var selected = this.options[this.selectedIndex].value;
    var unselected = selected == "enabled" ? "disabled" : "enabled";
    AJS.$( this.parentNode.parentNode ).addClass( "mm-" + selected ).removeClass( "mm-" + unselected ).toggleClass( "mm-changed" );
  });
});