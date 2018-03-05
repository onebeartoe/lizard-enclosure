
module paludariumScrubber(diameter = 40)
{
    difference()
    {
        cylinder(d=diameter,
             h=1);

         zTranslate = 1.0;
         step = 1;

         paludariumScrubber_yCutouts(step, diameter, zTranslate);

         paludariumScrubber_xCutouts(step, diameter, zTranslate);
    }
}

module paludariumScrubber_xCutouts(step, diameter, zTranslate)
{
    for(x = [-30 : step : 30])
    {
        translate([x,
                   -diameter/2.0,
                   zTranslate])
        rotate([0,45,0])
        cube([1, diameter+1, 1]);
    }
}

module paludariumScrubber_yCutouts(step, diameter, zTranslate)
{
    for(y = [-30 : step : 30])
    {
       translate([-diameter/2.0,
                  y,
                  zTranslate-0.75])
       rotate([45,0,0])
       cube([diameter+1, 1, 1]);
    }
}
