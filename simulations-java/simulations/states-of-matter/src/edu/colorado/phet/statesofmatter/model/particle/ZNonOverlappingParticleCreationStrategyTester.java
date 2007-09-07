package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;

import java.util.ArrayList;
import java.util.List;

public class ZNonOverlappingParticleCreationStrategyTester extends ZBoundedRandomParticleCreationStrategyTester {
    private static final double PARTICLE_RADIUS = 0.1;
    private static final int NUM_PARTICLES_TO_TEST = 100;

    public void setUp() {
        strategy = new NonOverlappingParticleCreationStrategy(StatesOfMatterConfig.CONTAINER_BOUNDS, PARTICLE_RADIUS);
    }

    public void testThatParticlesDoNotOverlap() {
        List particles = new ArrayList();

        for (int i = 0; i < NUM_PARTICLES_TO_TEST; i++) {
            StatesOfMatterParticle p1 = strategy.createNewParticle(particles, PARTICLE_RADIUS);

            for (int j = 0; j < particles.size(); j++) {
                StatesOfMatterParticle p2 = (StatesOfMatterParticle)particles.get(j);

                double deltaX = p1.getX() - p2.getX();
                double deltaY = p1.getY() - p2.getY();

                double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                assertTrue("Particles " + p1 + " and " + p2 + " overlap by " + (dist - PARTICLE_RADIUS) + "cm", dist >= 2 * PARTICLE_RADIUS);
            }

            particles.add(p1);
        }
    }
}
