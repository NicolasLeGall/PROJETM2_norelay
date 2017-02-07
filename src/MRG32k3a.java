

/*
	** This is a java adaptation of MRG32k3a.c**
	
   32-bits Random number generator U(0,1): MRG32k3a
   Author: Pierre L'Ecuyer,
   Source: Good Parameter Sets for Combined Multiple Recursive Random
           Number Generators,
           Shorter version in Operations Research,
           47, 1 (1999), 159--164.
   ---------------------------------------------------------
*/

public class MRG32k3a
{
	private static final double norm= 2.328306549295728e-10;
	private static final double m1=   4294967087.0;
	private static final double m2=   4294944443.0;
	private static final double a12=     1403580.0;
	private static final double a13n=     810728.0;
	private static final double a21=      527612.0;
	private static final double a23n=    1370589.0;
	
	/***
	The seeds for s10, s11, s12 must be integers in [0, m1 - 1] and not all 0. 
	The seeds for s20, s21, s22 must be integers in [0, m2 - 1] and not all 0. 
	***/
	
	private static final long SEED = 12345;
	
	private static double s10 = SEED, s11 = SEED, s12 = SEED,
								s20 = SEED, s21 = SEED, s22 = SEED;
	
	public double value;
	public int value_int;
	
	public MRG32k3a()
	{
		value = MRG32k3a();
	}
	
	public double rand()
	{
		value = MRG32k3a();
		return value;
	}
	
	public int rand_int()
	{
		value_int = (int) MRG32k3a();
		return value_int;
	}
	
	private static double MRG32k3a ()
	{
		long k;
		double p1, p2;
		/* Component 1 */
		p1 = a12 * s11 - a13n * s10;
		k = (long)(p1 / m1);
		p1 -= k * m1;
		if (p1 < 0.0)
				p1 += m1;
		s10 = s11;
		s11 = s12;
		s12 = p1;

		/* Component 2 */
		p2 = a21 * s22 - a23n * s20;
		k = (long)(p2 / m2);
		p2 -= k * m2;
		if (p2 < 0.0)
				p2 += m2;
		s20 = s21;
		s21 = s22;
		s22 = p2;

		/* Combination */
		if (p1 <= p2)
				return ((p1 - p2 + m1) * norm);
		else
				return ((p1 - p2) * norm);
	}
}