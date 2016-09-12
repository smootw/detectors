
//============================================================
// This script prints volumes for SVT detector
//============================================================
import org.jlab.geom.base.*;
import org.jlab.clasrec.utils.*;
import org.jlab.detector.geant4.*;

import org.jlab.detector.calib.utils.DatabaseConstantProvider;

import SVTFactory.SVTConstants;
import SVTFactory.SVTVolumeFactory;
import Misc.Util;

// todo: add getConstantsSVT() to DataBaseLoader?
//ConstantProvider cp = DataBaseLoader.getConstantsSVT();

// load the CCDB tables manually for now
//DatabaseConstantProvider cp = new DatabaseConstantProvider( 10, "default");
//cp.loadTable( SVTConstants.getCcdbPath() +"svt");
//cp.loadTable( SVTConstants.getCcdbPath() +"region");
//cp.loadTable( SVTConstants.getCcdbPath() +"support");
//cp.loadTable( SVTConstants.getCcdbPath() +"fiducial");
//cp.loadTable( SVTConstants.getCcdbPath() +"alignment");
//cp.loadTable( SVTConstants.getCcdbPath() +"material");
//cp.disconnect();
//cp.show();
//println cp.toString();

//System.exit(0);

SVTConstants.VERBOSE = true;
DatabaseConstantProvider cp = SVTConstants.connect( false );

SVTVolumeFactory factory = new SVTVolumeFactory( cp, false ); // ideal geometry
//SVTConstants.loadAlignmentShifts("shifts_test.dat"); // load alignment shifts from file
//factory.setApplyAlignmentShifts( true ); // shifted geometry
factory.makeVolumes();

Util.scaleDimensions( factory.getMotherVolume(), 0.5 );

def outFile = new File("bst__volumes_java.txt");
outFile.newWriter().withWriter{ w -> w << factory; }

factory.putParameters();

def axisName = ["x", "y", "z"];
def parFile = new File("bst__parameters_java.txt");
def writer=parFile.newWriter();

for( Map.Entry< String, String > entry : factory.getParameters().entrySet() )
{
  String key, value, unit;

  key = entry.getKey();
  value = entry.getValue();
  unit = "na";

  if( key[0] != "n" )
  {
    switch( key )
    {
    //case "sector0":
    //case "phiStart":
      //unit = factory.getProperty("unit_angle");
      //break;      
    default:
      unit = factory.getProperty("unit_length");
    }
  }
  
  writer << sprintf("%s | %s | %s | %s | %s | %s\n",
        key,
        value,
        unit,
        factory.getProperty("author"),
        factory.getProperty("email"),
        factory.getProperty("date")
        );
}

writer.close();
