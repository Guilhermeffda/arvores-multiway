public class ArvoreB {

    private int T; // Grau mínimo da árvore B
    private No raiz; // Raiz da árvore

    private static class No {
        int n; // Número atual de chaves
        int[] chaves; // Array de chaves
        No[] filhos; // Array de filhos
        boolean folha; // Se é folha ou não

        No(boolean folha, int T) {
            this.folha = folha;
            this.chaves = new int[2*T - 1];
            this.filhos = new No[2*T];
            this.n = 0;
        }
    }

    public ArvoreB(int t) {
        if (t < 2) {
            throw new IllegalArgumentException("Grau mínimo deve ser pelo menos 2");
        }
        this.T = t;
        this.raiz = new No(true, T);
    }

    public boolean estaVazia() {
        return raiz.n == 0;
    }

    // Método de busca
    public No buscar(int chave) {
        return buscar(raiz, chave);
    }

    private No buscar(No x, int chave) {
        int i = 0;
        while (i < x.n && chave > x.chaves[i]) {
            i++;
        }
        if (i < x.n && chave == x.chaves[i]) {
            return x;
        }
        if (x.folha) {
            return null;
        } else {
            return buscar(x.filhos[i], chave);
        }
    }

    // Método de inserção
    public void inserir(int chave) {
        No r = raiz;
        if (r.n == 2*T - 1) {
            No s = new No(false, T);
            raiz = s;
            s.filhos[0] = r;
            dividirFilho(s, 0, r);
            inserirNaoCompleto(s, chave);
        } else {
            inserirNaoCompleto(r, chave);
        }
    }

    private void inserirNaoCompleto(No x, int chave) {
        int i = x.n - 1;
        if (x.folha) {
            // Inserção em nó folha
            while (i >= 0 && chave < x.chaves[i]) {
                x.chaves[i+1] = x.chaves[i];
                i--;
            }
            x.chaves[i+1] = chave;
            x.n = x.n + 1;
        } else {
            // Inserção em nó interno
            while (i >= 0 && chave < x.chaves[i]) {
                i--;
            }
            i++;
            if (x.filhos[i].n == 2*T - 1) {
                dividirFilho(x, i, x.filhos[i]);
                if (chave > x.chaves[i]) {
                    i++;
                }
            }
            inserirNaoCompleto(x.filhos[i], chave);
        }
    }

    private void dividirFilho(No x, int i, No y) {
        No z = new No(y.folha, T);
        z.n = T - 1;
        // Copiar as últimas (T-1) chaves de y para z
        for (int j = 0; j < T-1; j++) {
            z.chaves[j] = y.chaves[j+T];
        }
        // Copiar os últimos T filhos de y para z
        if (!y.folha) {
            for (int j = 0; j < T; j++) {
                z.filhos[j] = y.filhos[j+T];
            }
        }
        // Reduzir o número de chaves em y
        y.n = T - 1;
        // Criar espaço para o novo filho
        for (int j = x.n; j >= i+1; j--) {
            x.filhos[j+1] = x.filhos[j];
        }
        // Vincular o novo filho a x
        x.filhos[i+1] = z;
        // Mover uma chave de y para x
        for (int j = x.n-1; j >= i; j--) {
            x.chaves[j+1] = x.chaves[j];
        }
        x.chaves[i] = y.chaves[T-1];
        x.n = x.n + 1;
    }

    // Método de remoção
    public void remover(int chave) {
        if (raiz == null) {
            return;
        }
        remover(raiz, chave);
        if (raiz.n == 0 && !raiz.folha) {
            raiz = raiz.filhos[0];
        }
    }

    private void remover(No x, int chave) {
        int idx = encontrarChave(x, chave);
        if (idx < x.n && x.chaves[idx] == chave) {
            if (x.folha) {
                removerDeFolha(x, idx);
            } else {
                removerDeNaoFolha(x, idx);
            }
        } else {
            if (x.folha) {
                System.out.println("A chave " + chave + " não existe na árvore");
                return;
            }
            boolean flag = (idx == x.n);
            if (x.filhos[idx].n < T) {
                preencher(x, idx);
            }
            if (flag && idx > x.n) {
                remover(x.filhos[idx - 1], chave);
            } else {
                remover(x.filhos[idx], chave);
            }
        }
    }

    private int encontrarChave(No x, int chave) {
        int idx = 0;
        while (idx < x.n && x.chaves[idx] < chave) {
            ++idx;
        }
        return idx;
    }

    private void removerDeFolha(No x, int idx) {
        for (int i = idx + 1; i < x.n; ++i) {
            x.chaves[i - 1] = x.chaves[i];
        }
        x.n--;
    }

    private void removerDeNaoFolha(No x, int idx) {
        int k = x.chaves[idx];
        if (x.filhos[idx].n >= T) {
            int pred = obterPredecessor(x, idx);
            x.chaves[idx] = pred;
            remover(x.filhos[idx], pred);
        } else if (x.filhos[idx + 1].n >= T) {
            int succ = obterSucessor(x, idx);
            x.chaves[idx] = succ;
            remover(x.filhos[idx + 1], succ);
        } else {
            fundir(x, idx);
            remover(x.filhos[idx], k);
        }
    }

    private int obterPredecessor(No x, int idx) {
        No cur = x.filhos[idx];
        while (!cur.folha) {
            cur = cur.filhos[cur.n];
        }
        return cur.chaves[cur.n - 1];
    }

    private int obterSucessor(No x, int idx) {
        No cur = x.filhos[idx + 1];
        while (!cur.folha) {
            cur = cur.filhos[0];
        }
        return cur.chaves[0];
    }

    private void preencher(No x, int idx) {
        if (idx != 0 && x.filhos[idx - 1].n >= T) {
            emprestarDoAnterior(x, idx);
        } else if (idx != x.n && x.filhos[idx + 1].n >= T) {
            emprestarDoProximo(x, idx);
        } else {
            if (idx != x.n) {
                fundir(x, idx);
            } else {
                fundir(x, idx - 1);
            }
        }
    }

    private void emprestarDoAnterior(No x, int idx) {
        No filho = x.filhos[idx];
        No irmao = x.filhos[idx - 1];
        for (int i = filho.n - 1; i >= 0; --i) {
            filho.chaves[i + 1] = filho.chaves[i];
        }
        if (!filho.folha) {
            for (int i = filho.n; i >= 0; --i) {
                filho.filhos[i + 1] = filho.filhos[i];
            }
        }
        filho.chaves[0] = x.chaves[idx - 1];
        if (!filho.folha) {
            filho.filhos[0] = irmao.filhos[irmao.n];
        }
        x.chaves[idx - 1] = irmao.chaves[irmao.n - 1];
        filho.n += 1;
        irmao.n -= 1;
    }

    private void emprestarDoProximo(No x, int idx) {
        No filho = x.filhos[idx];
        No irmao = x.filhos[idx + 1];
        filho.chaves[filho.n] = x.chaves[idx];
        if (!filho.folha) {
            filho.filhos[filho.n + 1] = irmao.filhos[0];
        }
        x.chaves[idx] = irmao.chaves[0];
        for (int i = 1; i < irmao.n; ++i) {
            irmao.chaves[i - 1] = irmao.chaves[i];
        }
        if (!irmao.folha) {
            for (int i = 1; i <= irmao.n; ++i) {
                irmao.filhos[i - 1] = irmao.filhos[i];
            }
        }
        filho.n += 1;
        irmao.n -= 1;
    }

    private void fundir(No x, int idx) {
        No filho = x.filhos[idx];
        No irmao = x.filhos[idx + 1];
        filho.chaves[T - 1] = x.chaves[idx];
        for (int i = 0; i < irmao.n; ++i) {
            filho.chaves[i + T] = irmao.chaves[i];
        }
        if (!filho.folha) {
            for (int i = 0; i <= irmao.n; ++i) {
                filho.filhos[i + T] = irmao.filhos[i];
            }
        }
        for (int i = idx + 1; i < x.n; ++i) {
            x.chaves[i - 1] = x.chaves[i];
        }
        for (int i = idx + 2; i <= x.n; ++i) {
            x.filhos[i - 1] = x.filhos[i];
        }
        filho.n += irmao.n + 1;
        x.n--;
    }

    // Método para imprimir a árvore (percurso em ordem)
    public void percursoEmOrdem() {
        percursoEmOrdem(raiz);
        System.out.println();
    }

    private void percursoEmOrdem(No x) {
        int i;
        for (i = 0; i < x.n; i++) {
            if (!x.folha) {
                percursoEmOrdem(x.filhos[i]);
            }
            System.out.print(x.chaves[i] + " ");
        }
        if (!x.folha) {
            percursoEmOrdem(x.filhos[i]);
        }
    }

    public static void main(String[] args) {
        ArvoreB arvore = new ArvoreB(3);
        System.out.println("Inserindo elementos na árvore B:");
        arvore.inserir(10);
        arvore.inserir(20);
        arvore.inserir(5);
        arvore.inserir(6);
        arvore.inserir(12);
        arvore.inserir(30);
        arvore.inserir(7);
        arvore.inserir(17);
        System.out.println("Árvore após inserções:");
        arvore.percursoEmOrdem();
        System.out.println("\nBuscando elementos:");
        System.out.println("Busca por 6: " + (arvore.buscar(6) != null ? "Encontrado" : "Não encontrado"));
        System.out.println("Busca por 15: " + (arvore.buscar(15) != null ? "Encontrado" : "Não encontrado"));
        System.out.println("\nRemovendo elementos:");
        arvore.remover(6);
        System.out.println("Árvore após remoção do 6:");
        arvore.percursoEmOrdem();
        arvore.remover(30);
        System.out.println("\nÁrvore após remoção do 30:");
        arvore.percursoEmOrdem();
    }
}