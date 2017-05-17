package network;

import java.util.ArrayList;
import java.util.List;

public class Layer {
    protected List<Neuron> list = new ArrayList<>();

    Layer(int neurons) {
        for (int i = 0; i < neurons; i++) {
            list.add(new Neuron());
        }
    }

    Layer(int neurons, Layer leftLayer) {
        for (int i = 0; i < neurons; i++) {
            list.add(new Neuron(leftLayer.getList()));
        }
    }

    private List<Neuron> getList() {
        return list;
    }

    public void setList(List<Neuron> list) {
        this.list = list;
    }
}
