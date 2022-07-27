package walletpkg

import (
	"crypto/ecdsa"
	"crypto/elliptic"
	"crypto/rand"
	"crypto/sha256"
	"encoding/json"
	"fmt"

	"github.com/btcsuite/btcutil/base58"

	"github.com/heoseoul/cabb/user/blockpkg"
	"github.com/heoseoul/cabb/user/txpkg"
	"golang.org/x/crypto/ripemd160"
)

type Wallet struct {
	PrvKey  ecdsa.PrivateKey //개인키
	PubKey  []byte           //공개키
	Address string           //지갑 주소
	Alias   string           //별칭
}

func (w *Wallet) FindAllTx(bs *blockpkg.Blocks, txs *txpkg.Txs) []*txpkg.Tx {
	return txs.FindTxByAddr(w.Address, bs)
}

type Wallets struct {
	WalletMap map[string]*Wallet
}

func NewWallet(alias string) *Wallet {
	wallet := &Wallet{}
	prvKey, bpubKey := newKeyPair()
	wallet.PubKey = HashPubKey(bpubKey)
	wallet.PrvKey = prvKey
	wallet.Alias = alias

	wallet.Address = encodeAddress(wallet.PubKey)
	return wallet
}

func CreateWallets() *Wallets {
	wallets := &Wallets{}
	wallets.WalletMap = make(map[string]*Wallet)
	return wallets
}

func (ws *Wallets) FindWallet(address string) *Wallet {
	return ws.WalletMap[address]
}

func encodeAddress(hashPub []byte) string {
	version := byte(0x00)
	s := base58.CheckEncode(hashPub, version)
	return s
}

func (ws *Wallets) SaveWallet(w *Wallet) string {
	ws.WalletMap[w.Address] = w
	return w.Address
}

func HashPubKey(pubKey []byte) []byte {
	publicSHA256 := sha256.Sum256(pubKey)
	RIPEMD160Hasher := ripemd160.New()
	RIPEMD160Hasher.Write(publicSHA256[:])
	publicRIPEMD160 := RIPEMD160Hasher.Sum(nil)
	return publicRIPEMD160
}

func newKeyPair() (ecdsa.PrivateKey, []byte) {
	curve := elliptic.P256()
	prvKey, _ := ecdsa.GenerateKey(curve, rand.Reader)
	pubKey := prvKey.PublicKey
	bpubKey := append(pubKey.X.Bytes(), pubKey.Y.Bytes()...)
	return *prvKey, bpubKey
}

func (w *Wallet) WalletPrint() {
	fmt.Printf("==========Wallet Info=============\n")
	fmt.Printf("Wallet: %s\nPrivate Key: %d\nPublic Key: %d\nWallet Address: %s\n\n", w.Alias, w.PrvKey, w.PubKey, w.Address)
}

func (w *Wallet) TxSign(tx *txpkg.Tx) {
	jsonTx, err := json.Marshal(tx)
	if err != nil {
		fmt.Println("json 변환 중 에러")
		return
	}
	shaTx := sha256.Sum256([]byte(jsonTx))
	signVal, _ := ecdsa.SignASN1(rand.Reader, &w.PrvKey, shaTx[:])
	tx.Sign = signVal
}
