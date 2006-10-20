cocoon.load("resource://org/apache/cocoon/forms/flow/javascript/Form.js");

var CLEAN_MODEL_XSL = "xsl/model/saveclean.xsl";
var form_;
var documentPath_;

// helper function
function loadAndShow(form, filename) {
	form.loadXML("cocoon:/" + filename + ".plain");
	form.showForm(filename + ".instance");
}

// this triggers the edit variant of the form (called from sitemap)
function editPage(form) {
	var baseURI = cocoon.parameters["baseURI"];
	var documentID = cocoon.parameters["documentID"];
	var filename = documentID + ".xml";
	
	// save form and page for auto-reload or AJAX calls
	form_ = form;
	documentPath_ = baseURI + filename;

	loadAndShow(form, filename);
	
	// the form includes all possible fields, but only some of them are
	// actually used. the best solution would be to strip out forms
	form.saveXML(baseURI + filename, CLEAN_MODEL_XSL);
	
	// reset form and page for auto-reload or AJAX calls
	form_ = null;
	documentPath_ = null;
	
	// show the display variant of the form
	cocoon.redirectTo(filename, false);
}

// this simply displays the form in readonly mode (called from sitemap)
function showPage(form) {
	var documentID = cocoon.parameters["documentID"];
	var filename = documentID + ".xml";
	
	// no auto-reload or AJAX calls are allowed in display mode
	form_ = null;
	documentPath_ = null;
	
	// set status to output only
	form.lookupWidget("/").setState(Packages.org.apache.cocoon.forms.formmodel.WidgetState.OUTPUT);
	
	loadAndShow(form, filename);
}

// called by auto-reload or AJAX for mutivalue fields
function upd(event) {
	if (form_ && documentPath_) {
		form_.saveXML(documentPath_, CLEAN_MODEL_XSL);
	}
	cocoon.exit();
}

// ok, I admit Javascript seriously rocks. I am overloading this function
// with another parameter to support transformations of the xml document
// before actually saving
Form.prototype.saveXML = function(uri, xsluri) {
    var source = null;
    var resolver = null;
    var outputStream = null;
    var xslsource = null;
    try {
        resolver = cocoon.getComponent(Packages.org.apache.cocoon.environment.SourceResolver.ROLE);
        source = resolver.resolveURI(uri);
        xslsource = new Packages.javax.xml.transform.stream.StreamSource(resolver.resolveURI(xsluri).getInputStream());
		//print(xslsource);

        var tf = Packages.javax.xml.transform.TransformerFactory.newInstance();

        if (source instanceof Packages.org.apache.excalibur.source.ModifiableSource
            && tf.getFeature(Packages.javax.xml.transform.sax.SAXTransformerFactory.FEATURE)) {

            outputStream = source.getOutputStream();
            var transformerHandler = tf.newTransformerHandler(xslsource);
            var transformer = transformerHandler.getTransformer();
            transformer.setOutputProperty(Packages.javax.xml.transform.OutputKeys.INDENT, "true");
            transformer.setOutputProperty(Packages.javax.xml.transform.OutputKeys.METHOD, "xml");
            transformerHandler.setResult(new Packages.javax.xml.transform.stream.StreamResult(outputStream));
            this.getXML().toSAX(transformerHandler);
        } else {
            throw new Packages.org.apache.cocoon.ProcessingException("Cannot write to source " + uri);
        }

    } finally {
        if (source != null)
            resolver.release(source);

        cocoon.releaseComponent(resolver);

        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (error) {
                cocoon.log.error("Could not flush/close outputstream: " + error);
            }
        }
    }
}