package backpropagation_network;

class Connection {
    private double connectionWeight = 0;
    private double previousDeltaWeight = 0;
    private double deltaWeight = 0;

    private Neuron leftNeuron;

    Connection(Neuron leftNeuron) {
        this.leftNeuron = leftNeuron;
    }

    public double getConnectionWeight() {
        return connectionWeight;
    }

    public void setConnectionWeight(double connectionWeight) {
        this.connectionWeight = connectionWeight;
    }

    public double getPreviousDeltaWeight() {
        return previousDeltaWeight;
    }

    public void setPreviousDeltaWeight(double previousDeltaWeight) {
        this.previousDeltaWeight = previousDeltaWeight;
    }

    public double getDeltaWeight() {
        return deltaWeight;
    }

    public void setDeltaWeight(double deltaWeight) {
        this.previousDeltaWeight = this.deltaWeight;
        this.deltaWeight = deltaWeight;
    }

    public Neuron getLeftNeuron() {
        return leftNeuron;
    }
}
